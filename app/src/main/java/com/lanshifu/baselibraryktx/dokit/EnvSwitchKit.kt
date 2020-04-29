package com.lanshifu.baselibraryktx.dokit

import com.didichuxing.doraemonkit.kit.AbstractKit
import android.content.Context
import com.didichuxing.doraemonkit.kit.Category
import com.didichuxing.doraemonkit.kit.Category.BIZ
import com.lanshifu.baselibraryktx.R
import com.lanshifu.lib.ext.logd


/**
 * @author lanxiaobin
 * @date 2020-04-30
 */
class EnvSwitchKit :AbstractKit(){
    override val category: Int
        get() = Category.BIZ
    override val icon: Int
        get() = R.mipmap.ic_launcher
    override val name: Int
        get() = R.string.test

    override fun onAppInit(context: Context?) {
        logd("EnvSwitchKit->onAppInit")
    }

    override fun onClick(context: Context?) {
        logd("EnvSwitchKit->onClick")
    }

}