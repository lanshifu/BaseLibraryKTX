package com.lanshifu.baselibraryktx.fragmentstatus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.lanshifu.lib.ext.logd

/**
 * @author lanxiaobin
 * @date 2020-04-27
 */
class TestFragment() : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        logd("TestFragment onCreate")
    }

    override fun onResume() {
        super.onResume()
        logd("TestFragment onResume,activity=$activity")
    }
}