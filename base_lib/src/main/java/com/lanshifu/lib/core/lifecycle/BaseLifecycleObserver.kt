package com.lanshifu.lib.core.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

/**
 * @author lanxiaobin
 * @date 2020-04-24
 *
 * LifecycleObserver 封装
 *
 * @param lifecycleOwner AppcompatActivity、Fragment都实现 LifecycleOwner
 */
open class BaseLifecycleObserver(val lifecycle: Lifecycle?) : LifecycleObserver,
    ILifecycleEvent {

    constructor(lifecycleOwner: LifecycleOwner?) : this(lifecycleOwner?.lifecycle)

    init {
        lifecycle?.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun _onActivityCreated() {
        onActivityCreated()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun _onStart() {
        onStart()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun _onResume() {
        onResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun _onStop() {
        onStop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun _onDestroy() {
        lifecycle?.removeObserver(this)
        onDestroy()
    }


    /**
     * 子类可以重写生命周期方法
     */
    override fun onActivityCreated() {
    }

    override fun onStart() {
    }

    override fun onResume() {
    }

    override fun onStop() {
    }

    override fun onDestroy() {
    }


}

interface ILifecycleEvent {
    fun onActivityCreated()
    fun onStart()
    fun onResume()
    fun onStop()
    fun onDestroy()
}