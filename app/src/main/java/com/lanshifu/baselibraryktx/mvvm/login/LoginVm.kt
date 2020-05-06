package com.lanshifu.baselibraryktx.mvvm.login

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.lanshifu.lib.base.BaseViewModel
import com.lanshifu.lib.ext.toast
import com.lanshifu.baselibraryktx.api.WanandroidFactory
import com.lanshifu.lib.ext.logd
import com.lanshifu.baselibraryktx.bean.BaseResponse
import luyao.util.ktx.bean.LoginResp
import rxhttp.toClass
import rxhttp.wrapper.param.RxHttp

class LoginVm : BaseViewModel<WanandroidFactory>(){

    var mResp: MutableLiveData<LoginResp> = MutableLiveData()

    var message = MutableLiveData<String>()

    init {
        message.postValue("我是包子")
        logd("我是包子")
    }

    fun login(account: String, password: String){

        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
            toast("账号密码不能为空")
            return
        }

        quickLaunch<LoginResp> {

            onStart {

            }
            request {
                mRepository.login(account,password)
            }

            onSuccess {
                mResp.value = it
            }


        }

    }
}