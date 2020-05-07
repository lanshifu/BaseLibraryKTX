package com.lanshifu.baselibraryktx.api

import com.lanshifu.baselibraryktx.bean.BaseResponse
import luyao.util.ktx.bean.LoginResp
import rxhttp.toClass
import rxhttp.wrapper.param.RxHttp

class WanandroidFactory {
    suspend fun login(account: String, password: String): BaseResponse<LoginResp> {

        return RxHttp.postForm(API.LOGIN)
            .setUrl("")
            .add("username", account)
            .add("password", password)
            .toClass<BaseResponse<LoginResp>>().await()
    }

}