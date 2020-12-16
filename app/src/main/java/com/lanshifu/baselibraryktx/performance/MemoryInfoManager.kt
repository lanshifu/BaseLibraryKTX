package com.lanshifu.baselibraryktx.performance

import android.app.ActivityManager
import android.content.Context
import android.os.*
import com.lanshifu.baselibraryktx.MyApplication

/**
 * @author lanxiaobin
 * @date 2020/12/17
 */
object MemoryInfoManager {

    private var mHandler: Handler? = null
    private val mHandlerThread by lazy { HandlerThread("MemoryInfoManager") }
    var memoryCallback: (memory: Int) -> Unit = {}
    private val mActivityManager: ActivityManager? by lazy {
        MyApplication.context?.getSystemService(
            Context.ACTIVITY_SERVICE
        ) as ActivityManager?
    }
    private val MSG_MEMORY: Int = 2

    var isStart = false

    /**
     * 当前使用内存
     */
    private var mLastMemoryRate = 0
    private const val NORMAL_SAMPLING_TIME = 1000L

    fun start() {
        isStart = true
        if (mHandler == null) {
            mHandlerThread.start()
            mHandler = object : Handler(mHandlerThread.looper) {
                override fun handleMessage(msg: Message) {
                    if (msg.what == MSG_MEMORY) {
                        executeMemoryData()
                        mHandler?.sendEmptyMessageDelayed(
                            MSG_MEMORY,
                            NORMAL_SAMPLING_TIME
                        )
                    }
                }
            }
        }

        mHandler?.sendEmptyMessageDelayed(
            MSG_MEMORY,
            NORMAL_SAMPLING_TIME
        )
    }

    fun stop() {
        isStart = false
        mHandler?.removeMessages(MSG_MEMORY)
        memoryCallback = {}
    }

    /**
     * 获取内存数值
     */
    private fun executeMemoryData() {
        mLastMemoryRate = getMemoryData()
        memoryCallback.invoke(mLastMemoryRate)
    }


    private fun getMemoryData(): Int {
        var mem = 0
        try {
            var memInfo: Debug.MemoryInfo? = null
            //28 为Android P
            if (Build.VERSION.SDK_INT > 28) {
                // 统计进程的内存信息 totalPss
                memInfo = Debug.MemoryInfo()
                Debug.getMemoryInfo(memInfo)
            } else {
                //As of Android Q, for regular apps this method will only return information about the memory info for the processes running as the caller's uid;
                // no other process memory info is available and will be zero. Also of Android Q the sample rate allowed by this API is significantly limited, if called faster the limit you will receive the same data as the previous call.
                val memInfos: Array<Debug.MemoryInfo>? =
                    mActivityManager?.getProcessMemoryInfo(intArrayOf(Process.myPid()))
                if (memInfos != null && memInfos.size > 0) {
                    memInfo = memInfos[0]
                }
            }
            val totalPss = memInfo!!.totalPss
            if (totalPss >= 0) {
                // Mem in MB
                mem = totalPss / 1024
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mem
    }
}