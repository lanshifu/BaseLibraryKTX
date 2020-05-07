package com.lanshifu.lib.ext

import android.util.Log
import com.lanshifu.lib.BuildConfig
import com.lanshifu.lib.Ktx
import com.lanshifu.lib.ext.sharedpreferences.getSpValue
import java.lang.StringBuilder


const val TAG = "lxb"

var showLog = getDebugStatus()

private fun getDebugStatus(): Boolean {
    if (BuildConfig.DEBUG) {
        return true
    }
    return try {
        Ktx.app.getSpValue("Lizhi_key_openlogd", false)
    } catch (e: Exception) {
        false
    }
}

var showStackTrace = true

private enum class LEVEL {
    V, D, I, W, E
}

fun String.logv(tag: String = TAG) = log(LEVEL.V, tag, this)
fun String.logd(tag: String = TAG) = log(LEVEL.D, tag, this)
fun String.logi(tag: String = TAG) = log(LEVEL.I, tag, this)
fun String.logw(tag: String = TAG) = log(LEVEL.W, tag, this)
fun String.loge(tag: String = TAG) = log(LEVEL.E, tag, this)

fun logv(tag: String = TAG, message: Any) = log(LEVEL.V, tag, message.toString())
fun logd(message: Any) = logd(TAG, message.toString())
fun logi(message: Any) = logi(TAG, message.toString())
fun logw(message: Any) = logw(TAG, message.toString())
fun loge(message: Any) = loge(TAG, message.toString())
fun logd(tag: String = TAG, message: Any) = log(LEVEL.D, tag, message.toString())
fun logi(tag: String = TAG, message: Any) = log(LEVEL.I, tag, message.toString())
fun logw(tag: String = TAG, message: Any) = log(LEVEL.W, tag, message.toString())
fun loge(tag: String = TAG, message: Any) = log(LEVEL.E, tag, message.toString())


private fun log(level: LEVEL, tag: String, message: String) {
    if (!showLog) return

    val tagBuilder = StringBuilder()
    tagBuilder.append(tag)

    if (showStackTrace) {
        val stackTrace = Thread.currentThread().stackTrace[5]
        tagBuilder.append(" ${stackTrace.methodName}(${stackTrace.fileName}:${stackTrace.lineNumber})")
    }
    when (level) {
        LEVEL.V -> Log.v(tagBuilder.toString(), message)
        LEVEL.D -> Log.d(tagBuilder.toString(), message)
        LEVEL.I -> Log.i(tagBuilder.toString(), message)
        LEVEL.W -> Log.w(tagBuilder.toString(), message)
        LEVEL.E -> Log.e(tagBuilder.toString(), message)
    }
}
