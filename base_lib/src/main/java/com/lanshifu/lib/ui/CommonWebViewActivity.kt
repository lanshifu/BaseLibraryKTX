package com.lanshifu.lib.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.lanshifu.lib.R
import com.lanshifu.lib.ext.toast
import com.lanshifu.lib.widget.CommonWebView
import kotlinx.android.synthetic.main.activity_common_webview.*

/**
 * @author lanxiaobin
 * @date 2020-03-13.
 */
class CommonWebViewActivity : AppCompatActivity() {

    companion object {
        val URL = "URL"
        val TAG = "CommonWebViewActivity"
    }

    var webView: CommonWebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_webview)

        initView()
    }

    fun initView() {
        btn_close.setOnClickListener {
            finish()
        }

        webView = CommonWebView(this)
        webView?.setBackgroundColor(0)
        fl_container.addView(webView)

        var url = intent.getStringExtra(URL)
        url?.let {
            webView?.loadUrl(it)
        } ?: toast("url为空")

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        webView?.let {
            if (event?.getAction() == KeyEvent.ACTION_DOWN) {
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
    var intent = Intent(this, CommonWebViewActivity::class.java)
    intent.putExtra(CommonWebViewActivity.URL, url)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
}