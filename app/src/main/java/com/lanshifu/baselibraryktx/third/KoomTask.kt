package com.lanshifu.baselibraryktx.third

import android.app.Application
import com.kwai.koom.javaoom.KOOM
import com.lanshifu.lib.ext.logi
import java.io.File

/**
 * @author lanxiaobin
 * @date 2020/8/15
 */
object KoomTask : ITask{
    override fun run(application: Application) {

        KOOM.init(application)
    }


    fun listenReportGenerateStatus(){

        KOOM.getInstance().setHeapReportUploader { file: File? ->

            logi("生成报告，fileName=${file?.name}")
        }
    }
}