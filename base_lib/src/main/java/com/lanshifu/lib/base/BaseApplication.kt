package com.lanshifu.lib.base

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.didichuxing.doraemonkit.DoraemonKit
import com.lanshifu.lib.ext.getCurrentProcessName
import com.lanshifu.lib.ext.logd
import com.tencent.mmkv.MMKV

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

        DoraemonKit.install(this)

        initMMKV()
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