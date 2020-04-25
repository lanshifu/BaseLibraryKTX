package com.lanshifu.baselibraryktx.mvvm.api

import com.lanshifu.baselibraryktx.mvvm.bean.BaseResponse
import luyao.util.ktx.bean.LoginResp

class WanandroidFactory {
    suspend fun login(account: String, password: String): BaseResponse<LoginResp> {
        return ServiceFactory.apiService.login(account, password)
    }

}