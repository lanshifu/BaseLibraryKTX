package com.lanshifu.baselibraryktx

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.lanshifu.lib.ext.logd

/**
 * @author lanxiaobin
 * @date 2020/8/18
 */
class ProcessLifecycleObserver : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onBackground() {
        logd("onBackground")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onForeground() {
        logd("onForeground")
    }
}