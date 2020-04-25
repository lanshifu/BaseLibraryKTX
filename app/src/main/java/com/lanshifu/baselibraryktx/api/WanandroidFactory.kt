package com.lanshifu.baselibraryktx.api

import com.lanshifu.baselibraryktx.bean.BaseResponse
import luyao.util.ktx.bean.LoginResp

class WanandroidFactory {
    suspend fun login(account: String, password: String): BaseResponse<LoginResp> {
        return ServiceFactory.apiService.login(account, password)
    }

}