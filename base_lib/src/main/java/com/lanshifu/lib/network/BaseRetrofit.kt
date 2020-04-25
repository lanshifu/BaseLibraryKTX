package com.lanshifu.lib.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object BaseRetrofit {


    var retrofitConfig: BaseRetrofitConfig = BaseRetrofitConfig()

    /**
     * 外部可以修改定制
     */
    fun initRetrofitConfig(retrofitConfig: BaseRetrofitConfig) {
        this.retrofitConfig = retrofitConfig
    }


    fun create(baseUrl: String): Retrofit {

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(retrofitConfig.initOkHttpClient()) //可以添加拦截器,可变长参数
            .build()
    }
}