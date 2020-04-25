package com.lanshifu.lib.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
 open class BaseRetrofitConfig : RetrofitConfig {

    lateinit var baseUrl: String

    override fun initRetrofit(): Retrofit = BaseRetrofit.create(baseUrl)

    override fun initOkHttpClient(vararg interceptors: Interceptor): OkHttpClient {
        return BaseOkHttpClient.create(*interceptors)
    }
}