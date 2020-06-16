package com.lanshifu.baselibraryktx

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.lanshifu.baselibraryktx.fragmentstatus.TestFragment
import com.lanshifu.baselibraryktx.gift.GiftSurfaceViewActivity
import com.lanshifu.baselibraryktx.mvvm.login.LoginActivity
import com.lanshifu.lib.core.lifecycle.LifecycleHandler
import com.lanshifu.lib.ext.logd
import com.lanshifu.lib.ext.reverseVisibility
import com.lanshifu.lib.ext.toast
import kotlinx.android.synthetic.main.activity_login.mBtnLogin
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xcrash.XCrash


class MainActivity : AppCompatActivity() {

    var testFragment: TestFragment? = null
    val handler = LifecycleHandler(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBtnLogin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
        btnBlurLayout.setOnClickListener {
            blurLayout.alpha = 1f
            blurLayout.reverseVisibility()
        }
        btnGift.setOnClickListener {
            startActivity(Intent(this,GiftSurfaceViewActivity::class.java))
        }

        ivLauncher.setOnClickListener {
            likeAnim.addLikeView()
        }

        btnNativeCrash.setOnClickListener {
            XCrash.testNativeCrash(false)
        }


        val drawable = ivBomb?.drawable as AnimationDrawable
        drawable.isOneShot = false
        drawable.start()

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

    override fun onStart() {
//        blurLayout.startBlur()
        super.onStart()
    }

    override fun onStop() {
//        blurLayout.pauseBlur()
        super.onStop()
    }
}
