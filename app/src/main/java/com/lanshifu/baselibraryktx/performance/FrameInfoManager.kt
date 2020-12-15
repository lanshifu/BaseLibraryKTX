package com.lanshifu.baselibraryktx.performance

import android.os.Handler
import android.view.Choreographer
import com.didichuxing.doraemonkit.config.DokitMemoryConfig

/**
 * @author lanxiaobin
 * @date 2020/12/16
 */
object FrameInfoManager {

    private val mMainHandler by lazy { Handler() }
    private const val FPS_SAMPLING_TIME = 1000L

    var frameCallback: (frame: Int) -> Unit = {}


    private val mRateRunnable: FrameRateRunnable = FrameRateRunnable()


    fun startMonitorFrameInfo() {
        DokitMemoryConfig.FPS_STATUS = true
        //开启定时任务
        mMainHandler.postDelayed(mRateRunnable, FPS_SAMPLING_TIME)
        Choreographer.getInstance().postFrameCallback(mRateRunnable)
    }

    fun stopMonitorFrameInfo() {
        DokitMemoryConfig.FPS_STATUS = false
        Choreographer.getInstance().removeFrameCallback(mRateRunnable)
        mMainHandler.removeCallbacks(mRateRunnable)
        frameCallback = {}
    }

    class FrameRateRunnable : Runnable, Choreographer.FrameCallback {

        private var totalFramesPerSecond = 0

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

