package com.lanshifu.baselibraryktx.gift

import android.graphics.BitmapFactory
import com.lanshifu.baselibraryktx.R
import com.lanshifu.baselibraryktx.gift.animationsurfaceview.ParabolaAnimationStrategy
import com.lanshifu.baselibraryktx.gift.animationsurfaceview.ScanAnimaitonStrategy
import com.lanshifu.lib.base.BaseActivity
import com.lanshifu.lib.core.lifecycle.LifecycleHandler
import com.lanshifu.lib.ext.dp2px
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
//            drawGift()
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


    private fun initScanAnimation() {
        val anim1 = ScanAnimaitonStrategy(animSurfaceView, dp2px(150), 2000)
        val anim2 = ParabolaAnimationStrategy(animSurfaceView, dp2px(150), 2000)
        anim1.icon = BitmapFactory.decodeResource(
            resources,
            R.mipmap.ic_launcher
        )
        anim2.icon = BitmapFactory.decodeResource(
            resources,
            R.mipmap.ic_launcher
        )

        animSurfaceView.addAnimation(anim1)
//        animSurfaceView.addAnimation(anim2)
//        animSurfaceView.setOnAnimationStausChangedListener(this)
        animSurfaceView.startAnimation()
    }
}