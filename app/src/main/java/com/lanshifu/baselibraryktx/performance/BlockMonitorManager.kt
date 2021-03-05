package com.lanshifu.baselibraryktx.performance

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.SystemClock
import android.text.TextUtils
import android.util.Printer
import com.lanshifu.lib.ext.logi
import com.lanshifu.lib.ext.logw
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author lanxiaobin
 * @date 2020/12/26
 */
object BlockMonitorManager {

    const val BLOCK_THRESHOLD_MILLIS = 200

    var printer = PrinterImpl()
    var isStart = false
    private val mStackDumpper by lazy { StackDumpper() }

    var blockCallback: (time: Long) -> Unit = {}

    fun start() {
        isStart = true

        Looper.getMainLooper().setMessageLogging(printer)
        logi("BlockMonitorManager start")
        mStackDumpper.startDump()
    }

    fun stop() {
        isStart = false

        Looper.getMainLooper().setMessageLogging(null)
        logi("BlockMonitorManager stop")
        mStackDumpper.stopDump()

        blockCallback = {}
    }


    class PrinterImpl : Printer {

        var mPrintingStarted = false
        private var mStartTime: Long = 0
        private var mStartThreadTime: Long = 0

        override fun println(x: String?) {

            //通过Handler 处理消息前后时间差，能确定执行耗时，来判断是否卡顿

            if (!mPrintingStarted) {
                //第一次
                mStartTime = System.currentTimeMillis()
                mStartThreadTime = SystemClock.currentThreadTimeMillis()
                mPrintingStarted = true
            } else {

                mPrintingStarted = false
                val endTime = System.currentTimeMillis()
                val endThreadTime = SystemClock.currentThreadTimeMillis()

                if (isBlock(endTime)) {
                    var threadStackEntries =
                        mStackDumpper.getThreadStackEntries(mStartTime, endTime)

                    logw("检测主线程卡顿，正在收集堆栈信息，耗时：${endTime - mStartTime},${threadStackEntries?.size}")
                    threadStackEntries?.forEach {
                        logw(it)
                    }
                    blockCallback?.invoke(endTime - mStartTime)
                }

            }

        }

        fun isBlock(endTime: Long): Boolean {
            return endTime - mStartTime > BLOCK_THRESHOLD_MILLIS
        }

    }

    class StackDumpper {

        private var mStackThread: HandlerThread? = null
        private var mStackHandler: Handler? = null
        private val DEFAULT_SAMPLE_INTERVAL = 300L
        private val DEFAULT_MAX_ENTRY_COUNT = 100
        private val mRunning = AtomicBoolean(false)
        private val SEPARATOR = "\r\n"
        private val sStackMap = LinkedHashMap<Long, String>()
        private var mFilterCache: String = ""
        private val TIME_FORMATTER =
            SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.CHINESE)

        init {


        }

        fun startDump() {

            if (mStackThread == null) {
                mStackThread = object : HandlerThread("BlockMonitor") {
                    override fun onLooperPrepared() {
                        mStackHandler = Handler(mStackThread!!.looper)
                        mStackHandler?.removeCallbacks(mRunnable)
                        mStackHandler?.postDelayed(mRunnable, DEFAULT_SAMPLE_INTERVAL)
                    }
                }
                mStackThread?.start()
            } else {
                mStackHandler?.removeCallbacks(mRunnable)
                mStackHandler?.postDelayed(mRunnable, DEFAULT_SAMPLE_INTERVAL)
            }

            mRunning.set(true)

        }

        fun stopDump() {
            mRunning.set(false)
            mStackHandler?.removeCallbacks(mRunnable)
        }


        fun getThreadStackEntries(
            startTime: Long,
            endTime: Long
        ): ArrayList<String>? {
            val result = ArrayList<String>()
            synchronized(sStackMap) {
                for (entryTime in sStackMap.keys) {
                    if (entryTime in (startTime + 1) until endTime) {
                        result.add(
                            TIME_FORMATTER.format(entryTime)
                                    + SEPARATOR
                                    + SEPARATOR
                                    + sStackMap[entryTime]
                        )
                    }
                }
            }
            return result
        }

        private val mRunnable: Runnable = object : Runnable {
            override fun run() {
                dumpInfo()
                if (mRunning.get()) {
                    mStackHandler?.postDelayed(this, DEFAULT_SAMPLE_INTERVAL)
                }
            }

            private fun dumpInfo() {

                val stringBuilder = StringBuilder()
                val thread = Looper.getMainLooper().thread
                for (stackTraceElement in thread.stackTrace) {
                    stringBuilder
                        .append(stackTraceElement.toString())
                        .append(SEPARATOR)
                }

                synchronized(sStackMap) {
                    if (sStackMap.size == DEFAULT_MAX_ENTRY_COUNT) {
                        sStackMap.remove(sStackMap.keys.iterator().next())
                    }
                    if (!shouldIgnore(stringBuilder)) {
                        sStackMap[System.currentTimeMillis()] = stringBuilder.toString()
                    }
                }

            }

            /**
             * 过滤掉重复项
             *
             * @param builder
             * @return
             */
            private fun shouldIgnore(builder: java.lang.StringBuilder): Boolean {
                if (TextUtils.equals(mFilterCache, builder.toString())) {
                    return true
                }
                mFilterCache = builder.toString()
                return false
            }
        }
    }
}

