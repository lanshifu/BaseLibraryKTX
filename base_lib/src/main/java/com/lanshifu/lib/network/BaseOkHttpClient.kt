package com.lanshifu.lib.network

import com.lanshifu.lib.network.intercept.LoggingIntercept
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


open class BaseOkHttpClient {


    // 读超时
    val READ_TIME_OUT = 20L
    // 写超时
    val WRITE_TIME_OUT = 30L
    // 连接超时
    val CONNECT_TIME_OUT = 5L

    // 初始化 okHttp
    fun create(interceptors: Array<Interceptor>? = null): OkHttpClient {

        val builder = OkHttpClient.Builder()

        interceptors?.forEach {
            builder.addInterceptor(it)
        }

        builder.addInterceptor(LoggingIntercept.init())
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
            .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)

        return builder.build()
    }
}