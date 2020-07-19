package com.lanshifu.baselibraryktx.hook

import android.util.Log
import com.taobao.android.dexposed.DexposedBridge
import com.taobao.android.dexposed.XC_MethodHook


/**
 * @author lanxiaobin
 * @date 2020/7/19
 */
object MethodHook {
    val TAG = "MethodHook"

    fun hookThread() {
        DexposedBridge.hookAllConstructors(Thread::class.java, object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun afterHookedMethod(param: MethodHookParam) {
                super.afterHookedMethod(param)
                val thread = param.thisObject as Thread
                val clazz: Class<*> = thread.javaClass
                if (clazz != Thread::class.java) {
                    Log.d(TAG, "found class extend Thread:$clazz")
                    DexposedBridge.findAndHookMethod(clazz, "run", ThreadMethodHook())
                }
                Log.d(
                    TAG,
                    "Thread: " + thread.name + " class:" + thread.javaClass + " is created."
                )
                Log.d(TAG,
                    "Thread:" + thread.name + "stack:" + Log.getStackTraceString(
                        Throwable()
                    )
                )
            }
        })
        DexposedBridge.findAndHookMethod(Thread::class.java, "run", ThreadMethodHook())
    }
}