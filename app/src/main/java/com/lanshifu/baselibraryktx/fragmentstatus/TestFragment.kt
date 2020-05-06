package com.lanshifu.baselibraryktx.fragmentstatus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.lanshifu.lib.ext.logd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author lanxiaobin
 * @date 2020-04-27
 */
class TestFragment() : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        logd("TestFragment onCreate")

        lifecycleScope.launch {

            withContext(Dispatchers.Default) {
                logd("TestFragment 进入协程代码块:${Thread.currentThread()}")
            }

            logd("TestFragment,协程代码块完成:${Thread.currentThread()}")

        }
    }

    override fun onResume() {
        super.onResume()
        logd("TestFragment onResume,activity=$activity")
    }
}