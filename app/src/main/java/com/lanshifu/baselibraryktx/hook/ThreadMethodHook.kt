package com.lanshifu.baselibraryktx.hook

import android.util.Log
import com.taobao.android.dexposed.XC_MethodHook

/**
 * @author lanxiaobin
 * @date 2020/7/19
 */
class ThreadMethodHook : XC_MethodHook() {
    @Throws(Throwable::class)
    override fun beforeHookedMethod(param: MethodHookParam) {
        super.beforeHookedMethod(param)
        val t = param.thisObject as Thread

        Log.i(TAG, "thread:$t, started..")
    }

    @Throws(Throwable::class)
    override fun afterHookedMethod(param: MethodHookParam) {
        super.afterHookedMethod(param)
        val t = param.thisObject as Thread
        Log.i(TAG, "thread:$t, exit..")
    }

    companion object {
        private const val TAG = "ThreadMethodHook"
    }
}