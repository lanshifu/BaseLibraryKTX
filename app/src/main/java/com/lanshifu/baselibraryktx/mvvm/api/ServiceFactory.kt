package com.lanshifu.baselibraryktx.mvvm.api

import com.lanshifu.lib.network.RetrofitFactory


/**
 * @author  hyzhan
 * @date    2019/5/28
 * @desc    TODO
 */
object ServiceFactory {

    val apiService by lazy { RetrofitFactory.create(ApiService::class.java) }
}