package com.lanshifu.baselibraryktx.api

import com.lanshifu.baselibraryktx.bean.BaseResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author lanxiaobin
 * @date 2020/9/27
 */
interface GitHubService {


    @GET("users/{user}/repos")
    fun listRepos(@Path("user") user: String?): Call<BaseResponse<String>>?
}