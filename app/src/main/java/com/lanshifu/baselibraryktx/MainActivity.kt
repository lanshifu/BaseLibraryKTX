package com.lanshifu.baselibraryktx

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.lanshifu.baselibraryktx.animations.SharedElementActivity
import com.lanshifu.baselibraryktx.banner.BannerActivity
import com.lanshifu.baselibraryktx.fragmentstatus.TestFragment
import com.lanshifu.baselibraryktx.gift.GiftSurfaceViewActivity
import com.lanshifu.baselibraryktx.list.DemoListActivity
import com.lanshifu.baselibraryktx.mvvm.login.LoginActivity
import com.lanshifu.baselibraryktx.native.NativeClass
import com.lanshifu.baselibraryktx.performance.FrameInfoManager
import com.lanshifu.baselibraryktx.record.RecordActivity
import com.lanshifu.baselibraryktx.shell.ShellTest
import com.lanshifu.baselibraryktx.threadtest.ThreadTest
import com.lanshifu.lib.base.BaseVMActivity
import com.lanshifu.lib.core.lifecycle.LifecycleHandler
import com.lanshifu.lib.ext.logd
import com.lanshifu.lib.ext.reverseVisibility
import com.lanshifu.lib.ext.toast
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_login.mBtnLogin
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.String
import java.util.*
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class MainActivity : BaseVMActivity<MainVM>() {

    var testFragment: TestFragment? = null
    val handler = LifecycleHandler(this)

    var excutor = ThreadPoolExecutor(2,2,60,TimeUnit.SECONDS,LinkedBlockingDeque())

    override fun getLayoutResId(): Int {
        return R.layout.activity_main
    }


    override fun initData() {

        mViewModel.smsCode.observe(this, Observer {
            logd("倒计时:$it")
        })
//        mViewModel.getSmsCode("")
    }

    override fun initView() {
        setContentView(R.layout.activity_main)

        mBtnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        btnBlurLayout.setOnClickListener {
            blurLayout.alpha = 1f
            blurLayout.reverseVisibility()
        }
        btnGift.setOnClickListener {
            startActivity(Intent(this, GiftSurfaceViewActivity::class.java))
        }

        ivLauncher.setOnClickListener {
            likeAnim.addLikeView()
        }

        btnNativeCrash.setOnClickListener {
//            XCrash.testNativeCrash(false)
//            CrashReport.testNativeCrash()
//            CrashReport.testJavaCrash()
//            NativeClass.crash()
            Thread{
                NativeClass.crash()
            }.start()
        }

        btnList.setOnClickListener {
            startActivity(Intent(this, DemoListActivity::class.java))
        }
        btnRecord.setOnClickListener {
            startActivity(Intent(this, RecordActivity::class.java))
        }

        btnBanner.setOnClickListener {
            startActivity(Intent(this, BannerActivity::class.java))
        }

        btnLeak.setOnClickListener {
            startActivity(Intent(this, LeakActivity::class.java))
        }

        btnAnim.setOnClickListener {
            startActivity(Intent(this, SharedElementActivity::class.java))
        }

        btnPermission.setOnClickListener {
            PermissionX.init(activity)
                .permissions(Manifest.permission.READ_CONTACTS, Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE)
                .onExplainRequestReason { scope, deniedList ->
                    scope.showRequestReasonDialog(deniedList, "Core fundamental are based on these permissions", "OK", "Cancel")
                }
                .onForwardToSettings { scope, deniedList ->
                    scope.showForwardToSettingsDialog(deniedList, "You need to allow necessary permissions in Settings manually", "OK", "Cancel")
                }
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        Toast.makeText(this, "All permissions are granted", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
                    }
                }
        }


        btnFps.setOnClickListener {
            FrameInfoManager.frameCallback = {
                btnFps.text = "帧率：$it"
            }
            FrameInfoManager.startMonitorFrameInfo()

        }

        val drawable = ivBomb?.drawable as AnimationDrawable
        drawable.isOneShot = false
        drawable.start()

        lifecycleScope.launch {
            var i = 0
            withContext(Dispatchers.Default) {
                Thread.sleep(30000)
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

//        getMemoryInfo()

        ProcessLifecycleOwner.get().lifecycle.addObserver(ProcessLifecycleObserver())

        ShellTest.isAvailableByPing("https://www.baidu.com/")

        ThreadTest.run()
        stackTest()
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

        } else {
            testFragment = fragment as TestFragment
            logd("TestFragment 不空")
        }


        logd("testFragmentStatus")
    }


    fun getMemoryInfo(){
        val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memoryInfo)
        logd(String.valueOf("getMemoryInfo:$memoryInfo.availMem / (1024 * 1024)}MB"))
        handler.postDelayed({getMemoryInfo()},2000)
    }


    var preStack = LinkedList<Int>()
    var nextStack = LinkedList<Int>()
    var currentPlaying = -1

    fun stackTest(){
        if (nextStack.isEmpty() && preStack.isEmpty()){
            nextStack.push(4)
            nextStack.push(3)
            nextStack.push(2)
            nextStack.push(1)
        }

        btnPre.setOnClickListener {
            if (preStack.isEmpty()){
                val removeLast = nextStack.removeLast()
                preStack.push(removeLast)

            }

            if (currentPlaying != -1){
                nextStack.push(currentPlaying)
            }
            currentPlaying = preStack.pop()
            toast("currentPlaying=$currentPlaying")
        }

        btnNext.setOnClickListener {
            if (nextStack.isEmpty()){
                toast("没有下一个了")
                nextStack.push(preStack.removeLast())
            }

            if (currentPlaying != -1){
                preStack.push(currentPlaying)
            }

            currentPlaying = nextStack.pop()
            toast("currentPlaying=$currentPlaying")
        }

        btnInsert.setOnClickListener {

        }


    }


    override fun onDestroy() {
        super.onDestroy()
        FrameInfoManager.stopMonitorFrameInfo()
    }
}
