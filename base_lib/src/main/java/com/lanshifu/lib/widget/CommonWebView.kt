package com.lanshifu.lib.widget

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.webkit.*
import android.widget.ProgressBar
import com.lanshifu.lib.Ktx
import com.lanshifu.lib.R
import com.lanshifu.lib.core.lifecycle.ActivityManager
import com.lanshifu.lib.ext.*


/**
 * @author lanxiaobin
 * @date 2020-03-13.
 */
open class CommonWebView : WebView {

    var progressbar: ProgressBar? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    fun init(context: Context) {
        initWebViewSettings()
        initView()
    }


    private fun initView() { //创建进度条
        progressbar = View.inflate(context, R.layout.horizon_progress, null) as ProgressBar
        //设置加载进度条的高度
        progressbar?.setLayoutParams(
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                dp2px(2)
            )
        )
        addView(progressbar)
        webChromeClient = WVChromeClient()
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                webView: WebView,
                webResourceRequest: WebResourceRequest
            ): Boolean {
                val url: String
                url = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    webResourceRequest.url.toString()
                } else {
                    webResourceRequest.toString()
                }
                try {
                    if (url.startsWith("weixin://")) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        ActivityManager.currentActivity?.startActivity(intent)
                        return true
                    }
                } catch (e: Exception) {
                    return false
                }

//                if (!url.startsWith("http") ||
//                    url.contains("baidupcs.com")  //百度云盘
//                ) {
//                    try {
//                        val intent = Intent(
//                            Intent.ACTION_VIEW,
//                            Uri.parse(url)
//                        )
//                        ActivityManager.currentActivity?.startActivity(intent)
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                } else {
//                    webView.loadUrl(url)
//                }
                return super.shouldOverrideUrlLoading(webView, webResourceRequest)
            }

            override fun onPageFinished(webView: WebView, s: String) {
                super.onPageFinished(webView, s)
//                setWebImageClick(this@X5WebView, JSCALLJAVA)
                mListener?.onPageFinish()
            }
        }

    }


    fun initWebViewSettings() {
        val webSetting = this.settings
        webSetting.javaScriptEnabled = true
        webSetting.javaScriptCanOpenWindowsAutomatically = true
        webSetting.allowFileAccess = true
        webSetting.setSupportZoom(false)
        webSetting.builtInZoomControls = false
        webSetting.useWideViewPort = false
        //webSetting.setSupportMultipleWindows(true); 打开这个就不会回调shouldOverrideUrlLoading
        webSetting.displayZoomControls = false
        //适应屏幕
        webSetting.defaultTextEncodingName = "utf-8"
        webSetting.domStorageEnabled = true
        webSetting.setGeolocationEnabled(true)
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE)
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webSetting.loadWithOverviewMode = true
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
//        LOAD_DEFAULT，根据cache-control决定是否从网络上取数据。
//        LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
//        如：m.taobao.com的cache-control为no-cache，在模式LOAD_DEFAULT下，无论如何都会从网络上取数据，如果没有网络，就会出现错误页面；在LOAD_CACHE_ELSE_NETWORK模式下，无论是否有网络，只要本地有缓存，都使用缓存。本地没有缓存时才从网络上获取。
//        m.sina.com.cn的cache-control为max-age=60，在两种模式下都使用本地缓存数据。
//        if (Ktx.app.isNetworkAvailable()) {
//            webSetting.cacheMode = WebSettings.LOAD_DEFAULT
//        } else {
//            webSetting.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
//        }
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE)
        webSetting.setAppCacheEnabled(false)
        webSetting.blockNetworkImage = false

        settings.setJavaScriptEnabled(true)
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE)
        settings.setDomStorageEnabled(false)
        settings.setSupportZoom(false)
        settings.setUseWideViewPort(false)
        settings.setDisplayZoomControls(false)
        isScrollContainer = false
        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
        settings.setDatabaseEnabled(false)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && 0 != context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) {
            setWebContentsDebuggingEnabled(true)
            "setWebContentsDebuggingEnabled（true）".logd()
        }
    }


    inner class WVChromeClient : WebChromeClient() {
        var mShowProgressBar = true
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            if (mShowProgressBar) {
                if (newProgress == 100) {
                    "newProgress:100".logv()
                    progressbar?.setProgress(100)
                    Ktx.handler.postDelayed({
                        progressbar?.gone()
                        progressbar?.progress = 0
                    }, 300)
                } else {
                    progressbar?.visible()
                    progressbar?.progress = newProgress
                    "newProgress:$newProgress".logv()
                }
                mListener?.onProgressChange(view, newProgress)
            }
            super.onProgressChanged(view, newProgress)
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            if (mListener != null) {
                mListener?.onReceivedTitle(title)
            }
            super.onReceivedTitle(view, title)
        }

        override fun onGeolocationPermissionsShowPrompt(
            origin: String?,
            callback: GeolocationPermissions.Callback?
        ) {
            callback?.invoke(origin, true, true);
            super.onGeolocationPermissionsShowPrompt(origin, callback)
        }


    }


    companion object {
        var mListener: OnWebViewListener? = null
    }

    //进度回调接口
    interface OnWebViewListener {
        fun onProgressChange(view: WebView?, newProgress: Int)
        fun onReceivedTitle(title: String?)
        fun onPageFinish()
    }
}