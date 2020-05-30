package com.lanshifu.baselibraryktx.gift.animationsurfaceview

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 * @author zhenghao.qi
 * @version 1.0
 * @time 2015年11月09日14:45:32
 */
class ScanAnimaitonStrategy(
    /**
     * 进行动画展示的view
     */
    private val animationSurfaceView: AnimationSurfaceView,
    /**
     * 起始点到终点的Y轴位移。
     */
    private val shift: Int,
    /**
     * 循环时间
     */
    private val cyclePeriod: Long) : IAnimationStrategy {
    /**
     * 起始X坐标
     */
    private var startX = 0

    /**
     * 起始Y坐标
     */
    private var startY = 0

    private var mX = 0.0
    private var mY = 0.0
    /**
     * X Y坐标。
     */
    override var x: Double
        get() = mX
        set(value) {}
    override var y: Double
        get() = mY
        set(value) {}

    /**
     * 动画开始时间。
     */
    private var startTime: Long = 0

    /**
     * 动画正在进行时值为true，反之为false。
     */
    private var doing = false

    val paint = Paint()

    override fun start() {
        startTime = System.currentTimeMillis()
        doing = true
    }

    /**
     * 设置起始位置坐标
     */
    private fun initParams() {
        val position = IntArray(2)
        animationSurfaceView.getLocationInWindow(position)
        startX = position[0]
        startY = position[1]

        paint.isAntiAlias = true
        paint.color = Color.CYAN
    }

    /**
     * 根据当前时间计算小球的X/Y坐标。
     */
    override fun compute() {
        val intervalTime = (System.currentTimeMillis() - startTime) % cyclePeriod
        val angle = Math.toRadians(360 * 1.0 * intervalTime / cyclePeriod)
        var y = (shift / 2 * Math.cos(angle)).toInt()
        y = Math.abs(y - shift / 2)
        this.mY = startY + y.toDouble()
        doing = true
    }

    override fun doing(): Boolean {
        return doing
    }

    override fun cancel() {
        doing = false
    }


    var icon: Bitmap? = null
    override fun draw(canvas: Canvas) {
        compute()
        // 绘上新图区域
//        canvas.drawRect(x, y, x + icon!!.width, y + icon!!.height, tempPaint)

        canvas.drawBitmap(icon!!, x.toFloat(), y.toFloat(), paint)

    }

    init {
        initParams()
    }
}