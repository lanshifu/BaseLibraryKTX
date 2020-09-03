package com.lanshifu.lib.widget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.lanshifu.lib.R
import com.lanshifu.lib.ext.gone
import com.lanshifu.lib.ext.visible
import kotlinx.android.synthetic.main.activity_webview.*

const val PARAM_TAG_WEB_VIEW_URL = "tagWebViewURL"
const val PARAM_TAG_WEB_VIEW_TITLE = "tagWebViewTitle"
const val PARAM_TAG_SHOW_TITLE = "showTitle"

class WebViewActivity : AppCompatActivity() {

    var mStrUrl = ""
    var mShowTitle = false

    var webView : CommonWebView? = null

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        intent?.let {
            mStrUrl = it.getStringExtra(PARAM_TAG_WEB_VIEW_URL)
            titleTextView.text = it.getStringExtra(PARAM_TAG_WEB_VIEW_TITLE)
            mShowTitle = it.getBooleanExtra(PARAM_TAG_SHOW_TITLE, true)
        }

        if (mShowTitle) {
            titleLayout.visible()
        } else {
            titleLayout.gone()
        }


        webView = PreloadWebView.getWebView(this)
        fl_container.addView(webView)

        webView?.loadUrl(mStrUrl)

        titleLayout.setOnClickListener() {
            finish()
        }

        initListener()
    }

    private fun initListener() {

        val listener = object : CommonWebView.OnWebViewListener{
            override fun onProgressChange(view: WebView?, newProgress: Int) {

            }

            override fun onReceivedTitle(title: String?) {

                if (titleTextView.text.isEmpty()){
                    title?.let {
                        titleTextView.text = it
                    }
                }
            }

            override fun onPageFinish() {
            }
        }

        webView?.mListener = listener
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        webView?.let {
            if (event?.action == KeyEvent.ACTION_DOWN) {
                //按返回键操作并且能回退网页
                if (keyCode == KeyEvent.KEYCODE_BACK && it.canGoBack()) {
                    //后退
                    it.goBack()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}


fun Context.startWebViewActivity(url: String) {
    var intent = Intent(this, WebViewActivity::class.java)
    intent.putExtra(PARAM_TAG_WEB_VIEW_URL, url)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
}

fun Activity.startWebViewActivity(url: String) {
    var intent = Intent(this, WebViewActivity::class.java)
    intent.putExtra(PARAM_TAG_WEB_VIEW_URL, url)
    startActivity(intent)
}