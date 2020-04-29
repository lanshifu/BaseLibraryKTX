package com.lanshifu.baselibraryktx.api

import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DoraemonInterceptor
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DoraemonWeakNetworkInterceptor
import com.lanshifu.lib.network.BaseOkHttpClient
import com.lanshifu.lib.network.intercept.LoggingIntercept
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class MyOkhttpConfig:BaseOkHttpClient() {


    fun initOkHttpClient(vararg interceptors: Interceptor): OkHttpClient {

        //添加拦截器

        var weakNetworkInterceptor = DoraemonWeakNetworkInterceptor()
        var doraemonInterceptor = DoraemonInterceptor()


        val builder = OkHttpClient.Builder()

        builder
            .addInterceptor(LoggingIntercept.init())
            .addNetworkInterceptor(weakNetworkInterceptor)
            .addInterceptor(doraemonInterceptor)
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
            .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)

        return builder.build()

    }

}