package com.lanshifu.baselibraryktx.gift

import android.graphics.BitmapFactory
import com.lanshifu.baselibraryktx.R
import com.lanshifu.baselibraryktx.gift.animationsurfaceview.AnimationSurfaceView
import com.lanshifu.baselibraryktx.gift.animationsurfaceview.ParabolaAnimationStrategy
import com.lanshifu.baselibraryktx.gift.animationsurfaceview.ScanAnimaitonStrategy
import com.lanshifu.lib.base.BaseActivity
import com.lanshifu.lib.core.lifecycle.LifecycleHandler
import com.lanshifu.lib.ext.dp2px
import com.lanshifu.lib.ext.logd
import kotlinx.android.synthetic.main.activity_giftsurvaceiew.*


/**
 * @author lanxiaobin
 * @date 2020/5/28
 */
class GiftSurfaceViewActivity : BaseActivity() {

    private val ASSET_V = "assets/json/v.json"

    val handler = LifecycleHandler(this)

    override fun getLayoutResId(): Int {
        return R.layout.activity_giftsurvaceiew
    }

    override fun initView() {


    }

    override fun initData() {

        handler.postDelayed({
            drawGift()

//            testAnimView()

            initScanAnimation()
        }, 1000)
    }

    private fun drawGift() {

        giftSurfaceView.setImageResource(R.mipmap.ic_launcher)
        giftSurfaceView.setPointScale(1f, 0, 0)
        giftSurfaceView.setRunTime(10000)
//        giftSurfaceView.setRandomPoint(9)
        giftSurfaceView.setListPoint(
            PointUtils.getListPointByResourceJson(this, ASSET_V)!!,
            10,
            true
        )
        giftSurfaceView.startAnim()
    }

    fun testAnimView() {
        animSurfaceView.setOnAnimationStausChangedListener(object :
            AnimationSurfaceView.OnStausChangedListener {
            override fun onAnimationEnd(view: AnimationSurfaceView?) {
                logd("onAnimationEnd")

            }

            override fun onAnimationStart(view: AnimationSurfaceView?) {
                logd("onAnimationStart")
            }
        })

        // 设置起始Y轴高度和终止X轴位移
        // 设置起始Y轴高度和终止X轴位移
        val iAnimationStrategy = ParabolaAnimationStrategy(animSurfaceView, dp2px(320), dp2px(320))
        animSurfaceView.setStrategy(iAnimationStrategy)
        animSurfaceView.icon = BitmapFactory.decodeResource(
            resources,
            R.mipmap.ic_launcher
        )
        animSurfaceView.startAnimation()
    }

    private fun initScanAnimation() {
        val iAnimationStrategy = ScanAnimaitonStrategy(animSurfaceView, dp2px(150), 2000)
        animSurfaceView.setStrategy(iAnimationStrategy)
//        animSurfaceView.setOnAnimationStausChangedListener(this)
        animSurfaceView.icon = BitmapFactory.decodeResource(
            resources,
            R.mipmap.ic_launcher
        )
        animSurfaceView.startAnimation()
    }
}