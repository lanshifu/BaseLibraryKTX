package com.lanshifu.lib.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.lanshifu.lib.core.util.Clazz
import com.lanshifu.lib.ext.logd


abstract class BaseVMFragment<VM : BaseViewModel<*>> : BaseFragment() {

    val mViewModel: VM by lazy { ViewModelProvider(this).get(Clazz.getClass<VM>(this)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        "onCreateView".logd()
        return inflater.inflate(getLayoutResId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        "onViewCreated".logd()
        initView()
        initData()
        startObserve()
        super.onViewCreated(view, savedInstanceState)
    }

    open fun startObserve() {
        mViewModel.mException.observe(this, Observer { it?.let { onError(it) } })
    }

    open fun onError(e: Throwable) {}


}