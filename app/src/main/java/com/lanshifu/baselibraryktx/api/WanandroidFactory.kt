package com.lanshifu.baselibraryktx.api

import com.lanshifu.baselibraryktx.bean.BaseResponse
import luyao.util.ktx.bean.LoginResp
import rxhttp.delay
import rxhttp.retry
import rxhttp.toClass
import rxhttp.wrapper.param.RxHttp

class WanandroidFactory {
    suspend fun login(account: String, password: String): BaseResponse<LoginResp> {

        return RxHttp.postForm(API.LOGIN)
            .add("username", account)
            .add("password", password)
            .toClass<BaseResponse<LoginResp>>()
            .retry(3,1000)
            .delay(1000)
            .await()
    }

}