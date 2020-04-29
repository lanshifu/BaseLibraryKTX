package com.lanshifu.lib.base

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.didichuxing.doraemonkit.DoraemonKit
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.lanshifu.lib.ext.getCurrentProcessName
import com.lanshifu.lib.ext.logd
import com.lanshifu.lib.network.BaseOkHttpClient
import com.tencent.mmkv.MMKV
import okhttp3.Interceptor
import rxhttp.wrapper.param.RxHttp

/**
 * @author lanxiaobin
 * @date 2020-04-25
 */
open class BaseApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        MultiDex.install(base)
    }

    override fun onCreate() {
        super.onCreate()

        initDoKit(pid = "2b82c70fb9f53c79a3e145f6ca52177f")

        initMMKV()

        initNetwork()
    }

    open fun initDoKit(list: List<AbstractKit>? = null, pid: String) {
        DoraemonKit.install(this, list, pid)
    }

    open fun initNetwork(interceptors: Array<Interceptor>? = null) {
        RxHttp.init(BaseOkHttpClient().create(interceptors))
    }

    private fun initMMKV() {
        val processName = getCurrentProcessName()
        val isMainProcess = applicationContext.packageName == processName
        if (isMainProcess) {
            logd("initMMKV")
            val rootDir = filesDir.absolutePath + "/mmkv"
            val dir = MMKV.initialize(rootDir)
            println("mmkv dir: $dir")
        }


    }

}