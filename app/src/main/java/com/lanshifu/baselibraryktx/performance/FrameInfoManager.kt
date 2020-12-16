package com.lanshifu.baselibraryktx.performance

import android.os.Handler
import android.view.Choreographer

/**
 * @author lanxiaobin
 * @date 2020/12/16
 */
object FrameInfoManager {

    private val mMainHandler by lazy { Handler() }
    private const val FPS_SAMPLING_TIME = 1000L

    var frameCallback: (frame: Int) -> Unit = {}
    var fpsOpen = false


    private val mRateRunnable: FrameRateRunnable = FrameRateRunnable()


    fun startMonitorFrameInfo() {
        fpsOpen = true
        //开启定时任务
        mMainHandler.postDelayed(mRateRunnable, FPS_SAMPLING_TIME)
        Choreographer.getInstance().postFrameCallback(mRateRunnable)
    }

    fun stopMonitorFrameInfo() {
        fpsOpen = false
        Choreographer.getInstance().removeFrameCallback(mRateRunnable)
        mRateRunnable.totalFramesPerSecond = 0
        mMainHandler.removeCallbacks(mRateRunnable)
        frameCallback = {}
    }

    class FrameRateRunnable : Runnable, Choreographer.FrameCallback {
        var totalFramesPerSecond = 0

        override fun run() {
            frameCallback.invoke(totalFramesPerSecond)
            totalFramesPerSecond = 0
            mMainHandler.postDelayed(mRateRunnable, FPS_SAMPLING_TIME)
        }

        override fun doFrame(frameTimeNanos: Long) {
            totalFramesPerSecond++
            Choreographer.getInstance().postFrameCallback(this)
        }

    }

}

