package com.lanshifu.baselibraryktx

import android.content.Context
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.lanshifu.baselibraryktx.dokit.BaiduDokit
import com.lanshifu.baselibraryktx.dokit.EnvSwitchKit
import com.lanshifu.baselibraryktx.dokit.PreloadWebviewDokit
import com.lanshifu.baselibraryktx.hook.MethodHook
import com.lanshifu.lib.base.BaseApplication
import com.lanshifu.lib.ext.logd
import io.alterac.blurkit.BlurKit
import okhttp3.Interceptor


class MyApplication : BaseApplication() {

    companion object {
        var context:Context? = null

    }

    override fun onCreate() {
        super.onCreate()
        context = this
        BlurKit.init(this)

//        XCrashTask.run(this)
//
//        BuglyTask.run(this)
//
        MethodHook.hookThread()
    }

    override fun initDoKit(list: MutableList<AbstractKit>, pid: String) {
        list.add(EnvSwitchKit())
        list.add(PreloadWebviewDokit())
        list.add(BaiduDokit())
        super.initDoKit(list, pid)
    }

    //可以自定义网络配置
    override fun initNetwork(interceptors: Array<Interceptor>?) {
        //可以添加拦截器,
        //interceptors
        logd("MyApp->initNetwork")

        super.initNetwork(interceptors)

    }

}