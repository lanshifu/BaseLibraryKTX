package com.lanshifu.baselibraryktx.mvvm.api

import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DoraemonInterceptor
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.DoraemonWeakNetworkInterceptor
import com.lanshifu.lib.network.BaseOkHttpClient
import com.lanshifu.lib.network.BaseRetrofit
import com.lanshifu.lib.network.BaseRetrofitConfig
import com.lanshifu.lib.network.intercept.LoggingIntercept
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class MyRetrofitConfig : BaseRetrofitConfig() {

    override fun initOkHttpClient(vararg interceptors: Interceptor): OkHttpClient {

        //添加拦截器

        var weakNetworkInterceptor = DoraemonWeakNetworkInterceptor()
        var doraemonInterceptor = DoraemonInterceptor()


        val builder = OkHttpClient.Builder()

        builder.addInterceptor(LoggingIntercept.init())
            .addNetworkInterceptor(weakNetworkInterceptor)
            .addInterceptor(doraemonInterceptor)
            .readTimeout(BaseOkHttpClient.READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(BaseOkHttpClient.WRITE_TIME_OUT, TimeUnit.SECONDS)
            .connectTimeout(BaseOkHttpClient.CONNECT_TIME_OUT, TimeUnit.SECONDS)

        return builder.build()

    }

    override fun initRetrofit(): Retrofit {
        return  BaseRetrofit.create(baseUrl)
    }
}