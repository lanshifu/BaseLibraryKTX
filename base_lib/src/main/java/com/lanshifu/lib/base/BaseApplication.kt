package com.lanshifu.lib.base

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import com.didichuxing.doraemonkit.DoraemonKit
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.lanshifu.lib.ext.getCurrentProcessName
import com.lanshifu.lib.ext.logd
import com.lanshifu.lib.network.BaseOkHttpClient
import com.tencent.mmkv.MMKV
import okhttp3.Interceptor
import org.json.JSONObject
import rxhttp.wrapper.param.RxHttp
import xcrash.ICrashCallback
import xcrash.TombstoneManager
import xcrash.TombstoneParser
import xcrash.XCrash
import xcrash.XCrash.InitParameters
import java.io.File
import java.io.FileWriter


/**
 * @author lanxiaobin
 * @date 2020-04-25
 */
open class BaseApplication : Application() {

    companion object{
        val TAG = "BaseApplication"
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        MultiDex.install(base)
        initXCrash()

    }

    override fun onCreate() {
        super.onCreate()

        initDoKit(pid = "2b82c70fb9f53c79a3e145f6ca52177f")

        initMMKV()

        initNetwork()
    }

    open fun initDoKit(list: MutableList<AbstractKit> = mutableListOf(), pid: String) {
        DoraemonKit.install(this, list, pid)
    }

    open fun initXCrash(){
//        xcrash.XCrash.init(this)

        Log.d(TAG, "xCrash SDK init: start")

        // callback for java crash, native crash and ANR
        val callback = ICrashCallback { logPath, emergency ->
            Log.d(TAG,
                "log path: " + (logPath ?: "(null)") + ", emergency: " + (emergency ?: "(null)")
            )
            if (emergency != null) {
                debug(logPath, emergency)

                // Disk is exhausted, send crash report immediately.
//                sendThenDeleteCrashLog(logPath, emergency)
            } else {
                // Add some expanded sections. Send crash report at the next time APP startup.

                // OK
                TombstoneManager.appendSection(logPath, "expanded_key_1", "expanded_content")
                TombstoneManager.appendSection(
                    logPath,
                    "expanded_key_2",
                    "expanded_content_row_1\nexpanded_content_row_2"
                )

                // Invalid. (Do NOT include multiple consecutive newline characters ("\n\n") in the content string.)
                // TombstoneManager.appendSection(logPath, "expanded_key_3", "expanded_content_row_1\n\nexpanded_content_row_2");
                debug(logPath, null)
            }
        }

        // Initialize xCrash.
        XCrash.init(
            this, InitParameters()
                .setAppVersion("1.2.3-beta456-patch789")
                .setJavaRethrow(true)
                .setJavaLogCountMax(10)
                .setJavaDumpAllThreadsWhiteList(
                    arrayOf(
                        "^main$",
                        "^Binder:.*",
                        ".*Finalizer.*"
                    )
                )
                .setJavaDumpAllThreadsCountMax(10)
                .setJavaCallback(callback)
                .setNativeRethrow(true)
                .setNativeLogCountMax(10)
                .setNativeDumpAllThreadsWhiteList(
                    arrayOf(
                        "^xcrash\\.sample$",
                        "^Signal Catcher$",
                        "^Jit thread pool$",
                        ".*(R|r)ender.*",
                        ".*Chrome.*"
                    )
                )
                .setNativeDumpAllThreadsCountMax(10)
                .setNativeCallback(callback)
                .setAnrRethrow(true)
                .setAnrLogCountMax(10)
                .setAnrCallback(callback)
                .setPlaceholderCountMax(3)
                .setPlaceholderSizeKb(512)
                .setLogFileMaintainDelayMs(1000)
        )

        Log.d(TAG, "xCrash SDK init: end")
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


    private fun debug(logPath: String, emergency: String?) {
        // Parse and save the crash info to a JSON file for debugging.
        var writer: FileWriter? = null
        try {
            val debug = File(applicationContext.filesDir.toString() + "/tombstones/debug.json")
            Log.d(TAG, "debug file=$debug")
            debug.createNewFile()
            writer = FileWriter(debug, false)
            val parce = TombstoneParser.parse(logPath, emergency) as Map<String,String>
            val toString = JSONObject(parce).toString()
            writer.write(toString)
            Log.i(TAG, "crash 日志已经写入到 $debug")
        } catch (e: Exception) {
            Log.d(TAG, "debug failed", e)
        } finally {
            if (writer != null) {
                try {
                    writer.close()
                } catch (ignored: Exception) {
                }
            }
        }
    }
}