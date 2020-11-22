package com.lanshifu.baselibraryktx.threadtest

import com.lanshifu.lib.ext.logi
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * @author lanxiaobin
 * @date 2020/11/22.
 */
object ThreadTest {

    var excutor = ThreadPoolExecutor(2,2,60, TimeUnit.SECONDS, LinkedBlockingDeque())

    fun run(){
        Thread {
            logi("Main 创建的线程")
            Thread.sleep(100000)
        }.start()

        excutor.execute {
            logi("excutor 创建的线程1")
            Thread.sleep(100000)
        }
        excutor.execute {
            logi("excutor 创建的线程2")
            Thread.sleep(100000)
        }
        excutor.execute {
            logi("excutor 创建的线程3")
            Thread.sleep(100000)
        }
    }
}