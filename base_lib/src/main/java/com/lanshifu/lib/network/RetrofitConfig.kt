package com.lanshifu.lib.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit


interface RetrofitConfig {

    fun initRetrofit(): Retrofit

    fun initOkHttpClient(vararg interceptors: Interceptor): OkHttpClient
}