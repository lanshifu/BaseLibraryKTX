package com.lanshifu.lib.network

import com.lanshifu.lib.annotation.BaseUrl

/**
 * retrofit 工厂类
 */

object RetrofitFactory {

    fun <T> create(clz: Class<T>): T {
//        prepareBaseUrl(clz)

        val retrofit = BaseRetrofit.retrofitConfig.initRetrofit()

        return retrofit.create(clz)
    }

    private fun <T> prepareBaseUrl(clz: Class<T>) {
        val baseUrlAnnotation = clz.getAnnotation(BaseUrl::class.java)
        val baseUrl = baseUrlAnnotation?.value ?: throw IllegalArgumentException("base url is null")
        BaseRetrofit.retrofitConfig.baseUrl = baseUrl
    }
}