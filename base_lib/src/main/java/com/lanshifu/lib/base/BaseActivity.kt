package com.lanshifu.lib.base

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.lanshifu.lib.R
import com.lanshifu.lib.ext.gone
import com.lanshifu.lib.ext.visible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel


abstract class BaseActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    protected var activity: AppCompatActivity? = null
    private var progressDialog: ProgressDialog? = null
    private val progressBar  by lazy { findViewById<ProgressBar>(R.id.loading) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        setContentView(getLayoutResId())
        initView()
//        setSupportActionBar(mToolbar)
        initData()
    }

    abstract fun getLayoutResId(): Int
    abstract fun initView()
    abstract fun initData()

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }


    fun showProgressDialog(title: String? = null, message: String = "加载中") {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this).apply {
                if (title != null) {
                    setTitle(title)
                }
                setMessage(message)
                setCancelable(false)
            }
        }
        progressDialog?.show()
    }

    fun hideProgressDialog() {
        progressDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    fun showLoading(){
        progressBar?.visible()
    }

    fun hideLoading(){
        progressBar?.gone()
    }

    /**
     * 实现灰白效果
     */
//    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
//        if ("FrameLayout" == name) {
//            val count = attrs.attributeCount
//            for (i in 0 until count) {
//                val attributeName = attrs.getAttributeName(i)
//                val attributeValue = attrs.getAttributeValue(i)
//                if (attributeName == "id") {
//                    val id = Integer.parseInt(attributeValue.substring(1))
//                    val idVal = resources.getResourceName(id)
//                    if ("android:id/content" == idVal) {
//                        return GrayFrameLayout(context, attrs)
//                    }
//                }
//            }
//        }
//        return super.onCreateView(name, context, attrs)
//    }
//
//    inner class GrayFrameLayout(context: Context, attrs: AttributeSet) :
//        FrameLayout(context, attrs) {
//        private val mPaint = Paint()
//
//        init {
//
//            val cm = ColorMatrix()
//            cm.setSaturation(0f)
//            mPaint.setColorFilter(ColorMatrixColorFilter(cm))
//        }
//
//        override fun dispatchDraw(canvas: Canvas) {
//            canvas.saveLayer(null, mPaint, Canvas.ALL_SAVE_FLAG)
//            super.dispatchDraw(canvas)
//            canvas.restore()
//        }
//
//
//        override fun draw(canvas: Canvas) {
//            canvas.saveLayer(null, mPaint, Canvas.ALL_SAVE_FLAG)
//            super.draw(canvas)
//            canvas.restore()
//        }
//
//    }
}