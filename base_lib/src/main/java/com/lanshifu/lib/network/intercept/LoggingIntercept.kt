package com.lanshifu.lib.network.intercept

import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor


object LoggingIntercept {

    fun init(): Interceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }
}