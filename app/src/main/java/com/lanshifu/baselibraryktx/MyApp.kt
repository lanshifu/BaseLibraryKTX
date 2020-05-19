package com.lanshifu.baselibraryktx

import com.didichuxing.doraemonkit.kit.AbstractKit
import com.lanshifu.baselibraryktx.dokit.EnvSwitchKit
import com.lanshifu.lib.base.BaseApplication
import com.lanshifu.lib.ext.logd
import io.alterac.blurkit.BlurKit
import okhttp3.Interceptor


class MyApp : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        BlurKit.init(this)
    }


    override fun initDoKit(list: List<AbstractKit>?, pid: String) {

        val kits = mutableListOf<AbstractKit>()
        kits.add(EnvSwitchKit())

        super.initDoKit(kits, pid)
    }

    //可以自定义网络配置
    override fun initNetwork(interceptors: Array<Interceptor>?) {
        //可以添加拦截器,
        //interceptors
        logd("MyApp->initNetwork")

        super.initNetwork(interceptors)

    }
}