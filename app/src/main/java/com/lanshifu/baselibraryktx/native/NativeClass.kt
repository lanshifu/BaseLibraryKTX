package com.lanshifu.baselibraryktx.native

/**
 * @author lanxiaobin
 * @date 2020/6/16
 */

object NativeClass {

    external fun createKey(): String
    external fun native_crash()

    init {
        System.loadLibrary("native-lib")
    }

    fun encrypt(json: String): String? {
        val password = createKey()
        return null
    }

    fun crash(){
        native_crash()
    }

}
