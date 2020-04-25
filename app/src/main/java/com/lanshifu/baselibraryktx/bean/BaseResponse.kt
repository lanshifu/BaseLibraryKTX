package com.lanshifu.baselibraryktx.bean

import com.lanshifu.lib.network.IBaseResponse


data class BaseResponse<T>(var data: T?,
                           var errorCode: Int = -1,
                           var errorMsg: String = "") : IBaseResponse<T> {

    override fun isSuccess(): Boolean = errorCode == 0

    override fun getKData(): T? = data

    override fun getKMessage(): String? = errorMsg
}