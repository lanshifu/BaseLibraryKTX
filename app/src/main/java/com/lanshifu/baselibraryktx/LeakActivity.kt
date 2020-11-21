package com.lanshifu.baselibraryktx

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lanshifu.lib.ext.logd

/**
 * @author lanxiaobin
 * @date 2020/10/22
 */
class LeakActivity : AppCompatActivity() {

    companion object {
        var innerClass: InnerClass? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_leak)

//        innerClassLeak()

        asyncTaskLeak()
    }

    private fun innerClassLeak() {
        innerClass = InnerClass()
        finish()
    }

    // 非静态内部类，持有外部类（LeakActivity）的引用
    inner class InnerClass {
        val activity = this@LeakActivity
    }

    //kotlin默认内部类相当于Java的静态内部类
    class StaticInnerClass {
//        val activity = this@LeakActivity //报错，不持有外部类引用
    }


    private fun asyncTaskLeak() {
//        object : AsyncTask<Void, Void, Int>() {
//            override fun doInBackground(vararg params: Void?): Int {
//                logd("asyncTaskLeak sleep...")
//                Thread.sleep(30000)
//                logd("asyncTaskLeak sleep end")
//                return 1
//            }
//
//        }.execute()

        MyAsyncTask().execute()

        finish()

    }

    class MyAsyncTask : AsyncTask<Void, Void, Int>(){
        override fun doInBackground(vararg params: Void?): Int {
            logd("asyncTaskLeak sleep...")
            Thread.sleep(30000)
            logd("asyncTaskLeak sleep end")
            return 1
        }
    }

}