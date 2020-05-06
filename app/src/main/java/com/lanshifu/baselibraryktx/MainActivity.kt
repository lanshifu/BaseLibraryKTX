package com.lanshifu.baselibraryktx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.lanshifu.lib.ext.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.lanshifu.baselibraryktx.fragmentstatus.TestFragment
import com.lanshifu.baselibraryktx.mvvm.login.LoginActivity
import com.lanshifu.lib.ext.logd
import kotlinx.android.synthetic.main.activity_login.*


class MainActivity : AppCompatActivity() {

    var testFragment: TestFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBtnLogin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }


        lifecycleScope.launch {
                var i = 0
                withContext(Dispatchers.Default) {
                    Thread.sleep(3000)
                    i = 3
                }

                toast("协程代码块执行完成,i=$i")
            }



        lifecycleScope.launch {
            var i = 0
            withContext(Dispatchers.Default) {
                Thread.sleep(4000)
                i = 3
            }

            toast("协程代码块执行完成2,i=$i")
        }


        testFragmentStatus()
    }

    private fun testFragmentStatus() {

        //方案1，设置不保存Fragment状态
//        testFragment.setRetainInstance(true)

        //方案2，恢复Fragment
        val fragment = supportFragmentManager.findFragmentByTag("TestFragment")
        if (fragment == null) {
            testFragment = TestFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.fl_container, testFragment!!, "TestFragment")
                .commit()

        }else{
            testFragment = fragment as TestFragment
            logd("TestFragment 不空")
        }


        logd("testFragmentStatus")
    }
}
