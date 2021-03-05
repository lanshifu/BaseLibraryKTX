package com.lanshifu.baselibraryktx.hook

import android.os.Parcel
import android.util.Log
import com.lanshifu.baselibraryktx.BuildConfig
import com.lanshifu.lib.ext.logd
import com.lanshifu.lib.ext.logi
import de.robv.android.xposed.DexposedBridge
import de.robv.android.xposed.XC_MethodHook


/**
 * @author lanxiaobin
 * @date 2020/7/19
 */
object MethodHook {
    val TAG = "MethodHook"

    fun hookThread() {
        if (!BuildConfig.DEBUG) {
            return
        }

        hookBinderProxy()

        DexposedBridge.hookAllConstructors(Thread::class.java, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                super.afterHookedMethod(param)
                val thread = param.thisObject as Thread
                val clazz: Class<*> = thread.javaClass
                if (clazz != Thread::class.java) {
                    Log.i(TAG, "found class extend Thread:$clazz")
//                    DexposedBridge.findAndHookMethod(clazz, "run", ThreadMethodHook())
                }
                Log.i(
                    TAG,
                    "Thread: " + thread.name + " class:" + thread.javaClass + " is created."
                )
                Log.d(
                    TAG,
                    "Thread:" + thread.name + "stack:" + Log.getStackTraceString(
                        Throwable()
                    )
                )
            }
        })
        DexposedBridge.findAndHookMethod(Thread::class.java, "run", ThreadMethodHook())
    }

    fun hookBinderProxy() {

        // hook BinderProxy 调用
        try {

            DexposedBridge.findAndHookMethod(
                Class.forName("android.os.BinderProxy"),
                "transact",
                Int::class.java,
                Parcel::class.java,
                Parcel::class.java,
                Int::class.java,
                BinderProxyMethodHook()
            )
        } catch (e: ClassNotFoundException) {
            e.printStackTrace();
        }
    }
}

class BinderProxyMethodHook : XC_MethodHook() {
    @Throws(Throwable::class)
    override fun beforeHookedMethod(param: MethodHookParam) {
        super.beforeHookedMethod(param)
        val t = param.thisObject

        logi("beforeHookedMethod,thisObject=$t")
        logd(
            "beforeHookedMethod,stack= " + Log.getStackTraceString(
                Throwable()
            )
        )

    }

}