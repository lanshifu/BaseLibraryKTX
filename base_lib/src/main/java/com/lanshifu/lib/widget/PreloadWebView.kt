package com.lanshifu.lib.widget

import android.content.Context
import android.content.MutableContextWrapper
import android.os.Looper
import com.lanshifu.lib.Ktx
import com.lanshifu.lib.ext.logd
import java.util.*


/**
 * @author lanxiaobin
 * @date 2020/9/3
 */
object PreloadWebView {

    private val CACHED_WEBVIEW_MAX_NUM = 2
    private val mCachedWebViewStack: Stack<CommonWebView> = Stack()

    /**
     * 创建WebView实例
     * 用了applicationContext
     */
    fun preload() {
        logd("PreloadWebView->preload")
        Looper.myQueue().addIdleHandler {
            if (mCachedWebViewStack.size < CACHED_WEBVIEW_MAX_NUM) {
                mCachedWebViewStack.push(createWebView())
            }
            false
        }
    }

    private fun createWebView(): CommonWebView {
        logd("PreloadWebView->createWebView")
        val commonWebView = CommonWebView(MutableContextWrapper(Ktx.app))
        commonWebView.loadUrl("file:///android_asset/preload.html")
        return commonWebView
    }


    private fun getHtml(): String? {
        val builder = StringBuilder()
        builder.append("<!DOCTYPE html>\n")
        builder.append("<html>\n")
        builder.append("<head>\n")
        builder.append("\">\n</head>\n")
        builder.append("</body>\n")
        builder.append("</html>\n")
        return builder.toString()
    }

    /**
     * 从缓存池中获取合适的WebView
     *
     * @param context activity context
     * @return WebView
     */
    fun getWebView(context: Context): CommonWebView {
        // 为空，直接返回新实例
        if (mCachedWebViewStack.isEmpty()) {
            val web = createWebView()
            val contextWrapper = web.context as MutableContextWrapper
            contextWrapper.baseContext = context
            preload()
            return web
        }
        val webView: CommonWebView = mCachedWebViewStack.pop()
        if (mCachedWebViewStack.isEmpty()){
            preload()
        }

        // webView不为空，则开始使用预创建的WebView,并且替换Context
        val contextWrapper = webView.context as MutableContextWrapper
        contextWrapper.baseContext = context
        logd("PreloadWebView->getWebView from cache")
        return webView
    }
}