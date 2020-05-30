package com.lanshifu.baselibraryktx.gift.animationsurfaceview

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log

/**
 * @author zhenghao.qi
 * @version 2015年11月10日10:40:03
 */
class ParabolaAnimationStrategy(
    private val animationSurfaceView: AnimationSurfaceView,
    h: Int,
    w: Int
) : IAnimationStrategy {
    /**
     * 起始下降高度。
     */
    private var height = 0

    /**
     * 起始点到终点的X轴位移。
     */
    private var width = 0

    /**
     * 水平位移速度。
     */
    private var velocity = 0.0

    /**
     * 动画开始时间。
     */
    private var startTime: Long = 0

    /**
     * 首阶段下载的时间。 单位：毫秒。
     */
    private var t1 = 0.0

    /**
     * 第二阶段上升与下载的时间。 单位：毫秒。
     */
    private var t2 = 0.0

    /**
     * 动画正在进行时值为true，反之为false。
     */
    private var doing = false

    private var mX: Double = 0.0
    private var mY: Double = 0.0
    override fun start() {
        startTime = System.currentTimeMillis()
        doing = true
    }


    override var x: Double
        get() = mX
        set(value) {}
    override var y: Double
        get() = mY
        set(value) {}

    /**
     * 设置起始下落的高度及水平位移宽度；以此计算水平初速度、计算小球下落的第一阶段及第二阶段上升耗时。
     */
    private fun setParams(h: Int, w: Int) {
        height = h
        width = w
        t1 = Math.sqrt(2 * height * 1.0 / GRAVITY)
        t2 =
            Math.sqrt((1 - WASTAGE) * 2 * height * 1.0 / GRAVITY)
        velocity = width * 1.0 / (t1 + 2 * t2)
        Log.d("Bruce1", "t1=$t1 t2=$t2")

        paint.isAntiAlias = true
        paint.color = Color.CYAN
    }

    /**
     * 根据当前时间计算小球的X/Y坐标。
     */
    override fun compute() {
        val used = (System.currentTimeMillis() - startTime) * 1.0 / 1000
        mX = velocity * used
        if (0 <= used && used < t1) {
            mY = height - 0.5 * GRAVITY * used * used
        } else if (t1 <= used && used < t1 + t2) {
            val tmp = t1 + t2 - used
            mY = (1 - WASTAGE) * height - 0.5 * GRAVITY * tmp * tmp
        } else if (t1 + t2 <= used && used < t1 + 2 * t2) {
            val tmp = used - t1 - t2
            mY = (1 - WASTAGE) * height - 0.5 * GRAVITY * tmp * tmp
        } else {
            Log.d("Bruce1", "used:$used set doing false")
            mX = velocity * (t1 + 2 * t2)
            mY = 0.0
            doing = false
        }
    }


    /**
     * 反转Y轴正方向。适应手机的真实坐标系。
     */
    fun getMirrorY(parentHeight: Int, bitHeight: Int): Double {
        val half = parentHeight shr 1
        var tmp = half + (half - y)
        tmp -= bitHeight.toDouble()
        return tmp
    }

    override fun doing(): Boolean {
        return doing
    }

    override fun cancel() {
        doing = false
    }

    val paint = Paint()
    var icon: Bitmap? = null
    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(icon!!, x.toFloat(), y.toFloat(), paint)
    }

    companion object {
        /**
         * 重力加速度值。
         */
        private const val GRAVITY = 400.78033f

        /**
         * 与X轴碰撞后，重力势能损失掉的百分比。
         */
        private const val WASTAGE = 0.3f
    }

    init {
        setParams(h, w)
    }
}