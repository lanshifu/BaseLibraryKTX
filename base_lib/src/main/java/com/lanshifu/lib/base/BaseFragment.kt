package com.lanshifu.lib.base

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel


/**
 * Fragment懒加载
 */
abstract class BaseFragment : androidx.fragment.app.Fragment(), CoroutineScope by MainScope() {

    private var progressDialog: ProgressDialog? = null

    protected var mRootView: View? = null

    private var mIsHasData = false//是否加载过数据

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (mRootView == null) {
            mRootView = inflater.inflate(getLayoutResId(), container, false)
        }
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onResume() {
        super.onResume()
        lazyLoadData()
    }

    private fun lazyLoadData() {
        if (!mIsHasData) {
            mIsHasData = true
            initData()
        }
    }

    abstract fun getLayoutResId(): Int

    abstract fun initView()

    abstract fun initData()

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }


    fun showProgressDialog(title: String? = null, message: String = "加载中") {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(activity).apply {
                if (title != null) {
                    setTitle(title)
                }
                setMessage(message)
                setCancelable(false)
            }
        }
        progressDialog?.show()
    }

    fun hideProgressDialog() {
        progressDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }
}