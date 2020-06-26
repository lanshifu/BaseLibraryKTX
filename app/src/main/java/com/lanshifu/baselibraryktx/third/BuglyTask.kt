package com.lanshifu.baselibraryktx.third

import android.app.Application
import android.content.Context
import android.os.Process
import android.text.TextUtils
import com.lanshifu.baselibraryktx.BuildConfig
import com.tencent.bugly.crashreport.CrashReport
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

/**
 * @author lanxiaobin
 * @date 2020/6/21
 */
object BuglyTask :ITask {

    override fun run(application: Application) {

        val context: Context = application
        // 获取当前包名
        val packageName: String = context.packageName
        // 获取当前进程名
        val processName: String? = getProcessName(Process.myPid())
        // 设置是否为上报进程
        val strategy = CrashReport.UserStrategy(context)
        strategy.isUploadProcess = processName == null || processName == packageName
        // 初始化Bugly
        CrashReport.initCrashReport(context, "e5d168a687", BuildConfig.DEBUG, strategy)
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private fun getProcessName(pid: Int): String? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName: String = reader.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim { it <= ' ' }
            }
            return processName
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            try {
                reader?.close()
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
        return null
    }
}