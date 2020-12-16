package com.lanshifu.baselibraryktx.performance

import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.text.TextUtils
import com.lanshifu.lib.ext.logd
import com.lanshifu.lib.ext.loge
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.RandomAccessFile

/**
 * @author lanxiaobin
 * @date 2020/12/16
 */
object CpuInfoManager {

    private var mHandler: Handler? = null
    private val mHandlerThread by lazy { HandlerThread("CpuInfoManager") }

    private val mAboveAndroidO by lazy { Build.VERSION.SDK_INT >= Build.VERSION_CODES.O }

    private var mLastCpuRate: Float = 1.0f

    var cpuCallback: (frame: Float) -> Unit = {}

    private val MSG_CPU: Int = 1

    var isStart = false

    /**
     * 信息采集时间 内存和cpu
     */
    private const val NORMAL_SAMPLING_TIME = 500L


    fun start() {
        isStart = true
        if (mHandler == null) {
            mHandlerThread.start()
            mHandler = object : Handler(mHandlerThread.looper) {
                override fun handleMessage(msg: Message) {
                    if (msg.what == MSG_CPU) {
                        executeCpuData()
                        mHandler?.sendEmptyMessageDelayed(MSG_CPU, NORMAL_SAMPLING_TIME)
                    }
                }
            }
        }

        mHandler?.sendEmptyMessageDelayed(MSG_CPU, NORMAL_SAMPLING_TIME)
    }

    fun stop() {
        isStart = false
        mHandler?.removeMessages(MSG_CPU)
        cpuCallback = {}
    }

    private fun executeCpuData() {
        if (mAboveAndroidO) {
            mLastCpuRate = getCpuDataForO()
        } else {
            mLastCpuRate = getCPUData()
        }
        cpuCallback.invoke(mLastCpuRate)
        logd("mLastCpuRate=$mLastCpuRate")
    }

    /**
     * 8.0以上获取cpu的方式
     *
     * @return
     */
    private fun getCpuDataForO(): Float {
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec("top -n 1")
            val reader =
                BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            var cpuIndex = -1
            while (reader.readLine().also { line = it } != null) {
                line = line?.trim { it <= ' ' }
                if (TextUtils.isEmpty(line)) {
                    continue
                }
                line?.let {line ->
                    val tempIndex: Int = getCPUIndex(line)
                    if (tempIndex != -1) {
                        cpuIndex = tempIndex
                        return@let
                    }
                    if (line.startsWith(android.os.Process.myPid().toString())) {
                        if (cpuIndex == -1) {
                            return@let
                        }
                        val param = line.split("\\s+".toRegex()).toTypedArray()
                        if (param.size <= cpuIndex) {
                            return@let
                        }
                        var cpu = param[cpuIndex]
                        if (cpu.endsWith("%")) {
                            cpu = cpu.substring(0, cpu.lastIndexOf("%"))
                        }
                        return cpu.toFloat() / Runtime.getRuntime().availableProcessors()
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            process?.destroy()
        }
        return 0f
    }

    private fun getCPUIndex(line: String): Int {
        if (line.contains("CPU")) {
            val titles = line.split("\\s+".toRegex()).toTypedArray()
            for (i in titles.indices) {
                if (titles[i].contains("CPU")) {
                    return i
                }
            }
        }
        return -1
    }


    var mProcStatFile: RandomAccessFile? = null
    var mAppStatFile: RandomAccessFile? = null
    var mLastAppCpuTime: Long = -1L
    var mLastCpuTime: Long = -1L

    /**
     * 8.0一下获取cpu的方式
     *
     * @return
     */
    private fun getCPUData(): Float {

        val cpuTime: Long
        val appTime: Long
        var value = 0.0f
        try {
            if (mProcStatFile == null || mAppStatFile == null) {
                mProcStatFile = RandomAccessFile("/proc/stat", "r")
                mAppStatFile =
                    RandomAccessFile("/proc/" + android.os.Process.myPid() + "/stat", "r")
            } else {
                mProcStatFile?.seek(0L)
                mAppStatFile?.seek(0L)
            }
            val procStatString: String = mProcStatFile?.readLine() ?: ""
            val appStatString: String = mAppStatFile?.readLine() ?: ""
            val procStats =
                procStatString.split(" ".toRegex()).toTypedArray()
            val appStats =
                appStatString.split(" ".toRegex()).toTypedArray()
            cpuTime = procStats[2].toLong() +
                    procStats[3].toLong() +
                    procStats[4].toLong() +
                    procStats[5].toLong() +
                    procStats[6].toLong() +
                    procStats[7].toLong() +
                    procStats[8].toLong()
            appTime = appStats[13].toLong() + appStats[14].toLong()
            if (mLastCpuTime == -1L && mLastAppCpuTime == -1L) {
                mLastCpuTime = cpuTime
                mLastAppCpuTime = appTime
                return value
            }
            value = (appTime - mLastAppCpuTime) * 100f / (cpuTime - mLastCpuTime)
            mLastCpuTime = cpuTime
            mLastAppCpuTime = appTime
            logd("cpuTime=$cpuTime,appTime=$appTime")
        } catch (e: Exception) {
            e.printStackTrace()
            loge("getCPUData,e=${e.message}")
        }
        return value
    }

}