package com.lanshifu.baselibraryktx.third

import android.app.Application
import android.util.Log
import com.lanshifu.lib.base.BaseApplication
import org.json.JSONObject
import xcrash.ICrashCallback
import xcrash.TombstoneManager
import xcrash.TombstoneParser
import xcrash.XCrash
import java.io.File
import java.io.FileWriter

/**
 * @author lanxiaobin
 * @date 2020/6/21
 */
object XCrashTask {

    var application: Application? = null
    fun run(application: Application) {
        XCrashTask.application = application
        initXCrash()
    }

    fun initXCrash() {
        Log.d(BaseApplication.TAG, "xCrash SDK init: start")

        // callback for java crash, native crash and ANR
        val callback = ICrashCallback { logPath, emergency ->
            Log.d(
                BaseApplication.TAG,
                "log path: " + (logPath ?: "(null)") + ", emergency: " + (emergency ?: "(null)")
            )
            if (emergency != null) {
                debug(
                    logPath,
                    emergency
                )

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
            application, XCrash.InitParameters()
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

        Log.d(BaseApplication.TAG, "xCrash SDK init: end")
    }


    private fun debug(logPath: String, emergency: String?) {
        // Parse and save the crash info to a JSON file for debugging.
        var writer: FileWriter? = null
        try {
            val debug = File(application?.filesDir.toString() + "/tombstones/debug.json")
            Log.d(BaseApplication.TAG, "debug file=$debug")
            debug.createNewFile()
            writer = FileWriter(debug, false)
            val parce = TombstoneParser.parse(logPath, emergency) as Map<String, String>
            val toString = JSONObject(parce).toString()
            writer.write(toString)
            Log.i(BaseApplication.TAG, "crash 日志已经写入到 $debug")
        } catch (e: Exception) {
            Log.d(BaseApplication.TAG, "debug failed", e)
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