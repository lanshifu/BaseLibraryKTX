package com.lanshifu.baselibraryktx.dokit

import android.content.Context
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.Category
import com.lanshifu.baselibraryktx.R
import com.lanshifu.lib.widget.startWebViewActivity


/**
 * @author lanxiaobin
 * @date 2020-04-30
 */
class BaiduDokit :AbstractKit(){
    override val category: Int
        get() = Category.BIZ
    override val icon: Int
        get() = R.mipmap.ic_launcher
    override val name: Int
        get() = R.string.preloadWebview

    override fun onAppInit(context: Context?) {
    }

    override fun onClick(context: Context?) {

       context?.startWebViewActivity("https://www.wanandroid.com/")
    }

}