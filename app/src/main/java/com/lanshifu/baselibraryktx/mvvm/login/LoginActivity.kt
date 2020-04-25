package com.lanshifu.baselibraryktx.mvvm.login

import androidx.lifecycle.Observer
import com.lanshifu.baselibraryktx.R
import com.lanshifu.lib.base.BaseVMActivity
import com.lanshifu.lib.ext.logd
import com.lanshifu.lib.ext.toast
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseVMActivity<LoginVm>() {


    override fun getLayoutResId(): Int {
        return R.layout.activity_login
    }

    override fun initView() {
        mBtnLogin.setOnClickListener {
            mViewModel.login(mTieAccount.text.toString(), mTiePassword.text.toString())
        }
    }

    override fun initData() {

    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.run {
            mResp.observe(this@LoginActivity, Observer {
                it?.run {
                    logd(it.toString())
                    toast(it.toString()
                    )
                }
            })

        }
    }
}