package com.lanshifu.baselibraryktx

import com.didichuxing.doraemonkit.kit.AbstractKit
import com.lanshifu.baselibraryktx.dokit.EnvSwitchKit
import com.lanshifu.baselibraryktx.hook.MethodHook
import com.lanshifu.baselibraryktx.third.BuglyTask
import com.lanshifu.baselibraryktx.third.XCrashTask
import com.lanshifu.lib.base.BaseApplication
import com.lanshifu.lib.ext.logd
import io.alterac.blurkit.BlurKit
import okhttp3.Interceptor


class MyApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        BlurKit.init(this)

        XCrashTask.run(this)

        BuglyTask.run(this)

        MethodHook.hookThread()
    }

    override fun initDoKit(list: MutableList<AbstractKit>, pid: String) {
        list.add(EnvSwitchKit())
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