package com.lanshifu.lib.network

import com.lanshifu.lib.ext.toast


/**
 * 封装了基本Response操作
 */
interface IBaseResponse<T> {

    fun isSuccess(): Boolean

    fun getKMessage(): String?

    fun getKData(): T?

    /**
     *  全局默认实现, 可根据自身业务 重写execute方法
     *  @param error            若有错误的回调, 默认 getKMessage()
     *  @param successResponse  成功的回调, 默认是返回 KResponse<T>
     */
    fun executeResp(
        successResponse: ((IBaseResponse<T>) -> Unit)?,
        error: ((String) -> Unit)? = null
    ) {

        if (this.isSuccess()) {
            successResponse?.invoke(this)
            return
        }

        (this.getKMessage() ?: "").let { error?.invoke(it) ?: toast(it) }
    }

    /**
     *  全局默认实现, 可根据自身业务 重写execute方法
     *  @param success          成功的回调, 默认是返回 getKData()
     *  @param error            若有错误的回调, 默认getKMessage()
     */
    fun execute(success: ((T?) -> Unit)?, error: ((String) -> Unit)? = null) {
        if (this.isSuccess()) {
            success?.invoke(this.getKData())
            return
        }

        (this.getKMessage() ?: "").let { error?.invoke(it) ?: toast(it) }
    }
}