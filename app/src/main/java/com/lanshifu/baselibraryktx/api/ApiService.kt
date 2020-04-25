package com.lanshifu.baselibraryktx.api

import com.lanshifu.lib.annotation.BaseUrl
import com.lanshifu.baselibraryktx.bean.BaseResponse
import luyao.util.ktx.bean.LoginResp

import retrofit2.http.POST
import retrofit2.http.Query


@BaseUrl(API.BASE_URL)
interface ApiService {

    @POST(API.LOGIN)
    suspend fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): BaseResponse<LoginResp>
}