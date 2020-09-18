package com.lanshifu.baselibraryktx.native

import com.lanshifu.lib.ext.toast

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

    fun crash(){

        native_crash()
    }

}
