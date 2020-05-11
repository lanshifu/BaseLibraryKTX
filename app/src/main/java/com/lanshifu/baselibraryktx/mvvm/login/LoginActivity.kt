package com.lanshifu.baselibraryktx.mvvm.login

import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.lanshifu.baselibraryktx.R
import com.lanshifu.lib.base.BaseVMActivity
import com.lanshifu.lib.base.mmkv.getMmkvValue
import com.lanshifu.lib.base.mmkv.putMmkvValue
import com.lanshifu.lib.core.lifecycle.LifecycleHandler
import com.lanshifu.lib.ext.logd
import com.lanshifu.lib.ext.sharedpreferences.putSpValue
import com.lanshifu.lib.ext.toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class LoginActivity : BaseVMActivity<LoginVm>() {


    override fun getLayoutResId(): Int {
        return R.layout.activity_login
    }

    override fun initView() {
        mBtnLogin.setOnClickListener {
            showProgressDialog()
            mViewModel.login(mTieAccount.text.toString(), mTiePassword.text.toString())
        }

        if (!getMmkvValue("isLogin",false)){
            putMmkvValue("isLogin",true)
        }


        getMmkvValue("isLogin",false)
    }

    override fun initData() {

        lifecycleScope.launch {

            withContext(Dispatchers.Default) {
                logd("LoginActivity 进入协程代码块:${Thread.currentThread()}")
                Thread.sleep(4000)
                logd("LoginActivity sleep完成")
            }

            logd("LoginActivity,协程代码块完成:${Thread.currentThread()}")

        }

        LifecycleHandler(this).postDelayed({
            logd("LifecycleHandler-do")
        },3000)

    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.run {
            mResp.observe(this@LoginActivity, Observer {
                hideProgressDialog()
                it?.run {
                    logd(it.toString())
                    toast(it.toString()

                    )
                }
            })

        }
    }
}