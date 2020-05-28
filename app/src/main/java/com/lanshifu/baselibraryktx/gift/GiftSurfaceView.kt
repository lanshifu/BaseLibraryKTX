package com.lanshifu.baselibraryktx.gift

import android.content.Context
import android.graphics.*
import android.os.SystemClock
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.annotation.DrawableRes
import com.lanshifu.baselibraryktx.gift.GiftSurfaceView.MODE
import com.lanshifu.baselibraryktx.gift.GiftSurfaceView.STATUS
import java.util.*

/**
 * GiftSurfaceView 最初出自于2014年开发HalloStar项目时所写，主要用于HalloStar项目直播间的送礼物动画。
 * 现在想来，那夕阳下的奔跑，是我逝去的青春。因前几天，刚高仿全民TV，所以想起，稍加改善，以此记录。
 *
 * @author Jenly [Jenly](mailto:jenly1314@gmail.com)
 * @since 2017/3/28
 */
class GiftSurfaceView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {
    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null
    private var paint: Paint? = null
    private var clearPaint: Paint? = null
    private var random: Random? = null
    private var point: Array<Point?> = arrayOfNulls(0)
    private var endPoint: Array<Point?> = arrayOfNulls(0)
    private var speeds: IntArray = IntArray(0)
    private var points: List<Point>? = null
    private var width = 0f
    private var height = 0f
    private var number = 0
    private var count = 0

    /**
     * 缩放比例
     */
    private var scale = 1f

    /**
     * 偏移（非随机情况下有效）
     */
    private var offsetX = 0
    private var offsetY = 0

    /**
     * 运行时间
     */
    private var runTime = 0

    /**
     * 开始时间
     */
    private var startTime = 0

    /**
     * 睡眠时间
     */
    private var sleepTime = 0

    /**
     * 是否处理（线程控制）
     */
    private var isDeal = false
    private var threadDeal: Thread? = null
    private var threadDraw: Thread? = null
    private var status = STATUS.DEFAULT

    enum class STATUS(private val mValue: Int) {
        DEFAULT(0),

        /**
         * 画
         */
        DRAWING(1),

        /**
         * 清空（清屏）
         */
        CLEAR(2),

        /**
         * 停止
         */
        STOP(3);

        companion object {
            fun getFromInt(value: Int): STATUS {
                for (status in values()) {
                    if (status.mValue == value) return status
                }
                return DEFAULT
            }
        }

    }

    private var mode = MODE.RANDOM

    enum class MODE(private val mValue: Int) {
        /**
         * 随机（随机递增）
         */
        RANDOM(0),

        /**
         * 键入（类似于打字拼成图案）
         */
        TYPING(1),

        /**
         * 移动（随机点初始位置，然后拼成图案）
         */
        MOVE(2);

        companion object {
            fun getFromInt(value: Int): MODE {
                for (status in values()) {
                    if (status.mValue == value) return status
                }
                return RANDOM
            }
        }

    }

    private fun initData() {
        paint = Paint()
        //paint抗锯齿
        paint!!.isAntiAlias = true
        clearPaint = Paint()
        clearPaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        random = Random()

        //设置透明
        setZOrderOnTop(true)
        holder.setFormat(PixelFormat.TRANSLUCENT)
        holder.addCallback(this)
        setLayerType(View.LAYER_TYPE_NONE, null)
    }

    //----------------------
    private fun getBitmapByResource(@DrawableRes resId: Int): Bitmap {
        return BitmapFactory.decodeResource(resources, resId)
    }

    fun setImageResource(@DrawableRes resId: Int) {
        setImageBitmap(getBitmapByResource(resId))
    }

    /**
     * @param resId
     * @param scale 缩放（图片）
     */
    fun setImageResource(@DrawableRes resId: Int, scale: Float) {
        setImageBitmap(getBitmapByResource(resId), scale)
    }

    fun setImageBitmap(bitmap: Bitmap?) {
        this.bitmap = bitmap
    }

    /**
     * @param bitmap
     * @param scale  缩放（图片）
     */
    fun setImageBitmap(bitmap: Bitmap, scale: Float) {
        this.bitmap = scaleBitmap(bitmap, scale)
    }

    /**
     * 拼图的终点坐标的整体缩放
     *
     * @param scale   点坐标整体缩放
     * 1.0f 表示坐标
     * @param offsetX X 轴偏移量
     * @param offsetY Y 轴偏移量
     */
    fun setPointScale(scale: Float, offsetX: Int, offsetY: Int) {
        this.scale = scale
        this.offsetX = offsetX
        this.offsetY = offsetY
    }

    /**
     * 运行时间
     * LONG_TIME 表示长久运行（不会消失）
     *
     * @param runTime
     */
    fun setRunTime(runTime: Int) {
        this.runTime = runTime
    }

    /**
     * 随机点
     *
     * @param num 随机点的数量
     */
    fun setRandomPoint(num: Int) {
        setRandomPoint(num, DEFAULT_RANDOM_SLEEP_TIME)
    }

    /**
     * 随机点
     *
     * @param num       随机点的数量
     * @param sleepTime 线程睡眠时间
     */
    fun setRandomPoint(num: Int, sleepTime: Int) {
        number = num
        mode = MODE.RANDOM
        this.sleepTime = sleepTime
    }

    fun setListPoint(list: List<Point>) {
        setListPoint(list, DEFAULT_DRAWING_SLEEP_TIME, false)
    }

    fun setListPoint(
        list: List<Point>,
        isTyping: Boolean
    ) {
        setListPoint(list, DEFAULT_DRAWING_SLEEP_TIME, isTyping)
    }

    /**
     * 设置点集合
     *
     * @param list
     * @param sleepTime
     * @param isTyping
     */
    fun setListPoint(
        list: List<Point>,
        sleepTime: Int,
        isTyping: Boolean
    ) {
        mode = if (isTyping) MODE.TYPING else MODE.MOVE
        points = list
        this.sleepTime = sleepTime
        number = list.size
    }

    fun updateGiftSurfaceViewParams(width: Int, height: Int) {
        this.width = width.toFloat()
        this.height = height.toFloat()
        if (width == 0 || height == 0) {
            val displayMetrics = context.resources.displayMetrics
            this.width = displayMetrics.widthPixels.toFloat()
            this.height = displayMetrics.heightPixels.toFloat()
        }
        if (mode == MODE.MOVE) {
            updatePoints(points)
        } else {
            count = 0
            point = arrayOfNulls(number)
            for (i in 0 until number) {
                if (mode == MODE.TYPING) {
                    point[i] = Point(
                        (points!![i].x * scale).toInt() + offsetX,
                        ((points!![i].y + offsetY) * scale).toInt()
                    )
                } else {
                    point[i] = random()
                }
            }
        }
    }

    private fun updatePoints(points: List<Point>?) {
        point = arrayOfNulls(number)
        endPoint = arrayOfNulls(number)
        speeds = IntArray(number)
        count = number
        for (i in 0 until number) {
            endPoint[i] = Point(
                (points!![i].x * scale).toInt() + offsetX,
                ((points[i].y + offsetY) * scale).toInt()
            )
            point[i] = random()
            val temp1 = Math.abs(point[i]!!.x - endPoint[i]!!.x)
            val temp2 = Math.abs(point[i]!!.y - endPoint[i]!!.y)
            speeds[i] = ((Math.max(temp1, temp2) shr 6) * scale).toInt()
            if (speeds[i] < MIN_SPEED) {
                speeds[i] = MIN_SPEED
            }
        }
    }

    /**
     * 移动逻辑
     */
    private fun moveLogic() {
        for (i in 0 until number) {
            if (Math.abs(point[i]!!.x - endPoint[i]!!.x) < speeds[i]) {
                point[i]!!.x = endPoint[i]!!.x
            } else if (point[i]!!.x < endPoint[i]!!.x) {
                point[i]!!.x += speeds[i]
            } else if (point[i]!!.x > endPoint[i]!!.x) {
                point[i]!!.x -= speeds[i]
            }
            if (Math.abs(point[i]!!.y - endPoint[i]!!.y) < speeds[i]) {
                point[i]!!.y = endPoint[i]!!.y
            } else if (point[i]!!.y < endPoint[i]!!.y) {
                point[i]!!.y += speeds[i]
            } else if (point[i]!!.y > endPoint[i]!!.y) {
                point[i]!!.y -= speeds[i]
            }
        }
    }

    /**
     * 随机一个点
     *
     * @return
     */
    private fun random(): Point {
        var x = 0
        var y = 0
        if (width >= bitmap!!.width && height >= bitmap!!.width) {
            x = random!!.nextInt((width - bitmap!!.width * 2).toInt()) + bitmap!!.width
            y = random!!.nextInt((height - bitmap!!.height * 2).toInt()) + bitmap!!.height
        }
        return Point(x, y)
    }

    private var animHight = 600

    /**
     * 画图
     */
    private fun drawBitmap() {
        try {
            if (canvas != null) {
                clearScreen()
                canvas!!.drawBitmap(
                    bitmap!!,
                    300 - bitmap!!.width * .5f,
                    animHight - bitmap!!.height * .5f,
                    paint
                )
                animHight -= 5
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 清屏
     */
    private fun clearScreen() {
        if (canvas != null) {
            canvas!!.drawPaint(clearPaint!!)
        }
    }

    /**
     * 递增逻辑
     */
    private fun increaseLogic() {
        if (count < point.size) {
            count++
        }
    }

    private fun runLogic(mode: MODE) {
        when (mode) {
            MODE.RANDOM, MODE.TYPING -> increaseLogic()
            MODE.MOVE -> moveLogic()
        }
    }

    /**
     * 停止线程逻辑
     */
    private fun stopLogic() {
        isDeal = false
        try {
            if (threadDeal != null) {
                threadDeal!!.join()
            }
            if (threadDraw != null) {
                threadDraw!!.join()
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    /**
     * 画图Runnable
     */
    var runDraw = Runnable {
        try {
            while (isDeal) {
                canvas = holder.lockCanvas()
                when (status) {
                    STATUS.DRAWING -> {
                        if (mode == MODE.MOVE) {
                            clearScreen()
                        }
                        drawBitmap()
                    }
                    STATUS.CLEAR -> {
                        clearScreen()
                        status = STATUS.STOP
                    }
                    STATUS.STOP -> clearScreen()
                    else -> {
                    }
                }
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
                SystemClock.sleep(sleepTime.toLong())
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * 逻辑Runnable
     */
    var runDeal = Runnable {
        while (isDeal) {
            when (status) {
                STATUS.DRAWING -> runLogic(mode)
                STATUS.CLEAR -> {
                }
                STATUS.STOP -> stopLogic()
                else -> {
                }
            }
            SystemClock.sleep(sleepTime.toLong())
            if (runTime != LONG_TIME) { //是否长久运行
                startTime += sleepTime
                if (startTime >= runTime) { //自动结束
                    clearAndStop()
                }
            }
        }
    }

    /**
     * 启动线程
     */
    private fun start() {
        try {
            startTime = 0
            isDeal = true
            status = STATUS.DRAWING
            threadDeal = Thread(runDeal)
            threadDraw = Thread(runDraw)
            threadDeal!!.start()
            threadDraw!!.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 清空并停止线程（通过状态）
     */
    fun clearAndStop() {
        status = STATUS.CLEAR
    }

    /**
     * 停止线程（通过状态）
     */
    private fun stop() {
        status = STATUS.STOP
    }

    //----------------------
    override fun surfaceCreated(holder: SurfaceHolder) {}
    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int
    ) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {}
    fun startAnim() {
        updateGiftSurfaceViewParams(600, 600)
        start()
    }

    /**
     * 图片按指定宽高缩放
     *
     * @param bmp
     * @param width
     * @param height
     * @return
     */
    fun scaleBitmap(bmp: Bitmap?, width: Int, height: Int): Bitmap {
        val m = Matrix()
        m.postScale(width.toFloat(), height.toFloat())
        return Bitmap.createScaledBitmap(bmp!!, width, height, true)
    }

    /**
     * 图片按比例缩放
     *
     * @param bmp
     * @param scale
     * @return
     */
    private fun scaleBitmap(bmp: Bitmap, scale: Float): Bitmap {
        val width = (bmp.width * scale).toInt()
        val height = (bmp.height * scale).toInt()
        val m = Matrix()
        m.postScale(width.toFloat(), height.toFloat())
        return Bitmap.createScaledBitmap(bmp, width, height, true)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isDeal = false
    }

    companion object {
        private const val TAG = "GiftSurfaceView"

        /**
         * 最小速度
         */
        private const val MIN_SPEED = 4
        const val LONG_TIME = 0

        /**
         * 随机模式默认睡眠时间
         */
        private const val DEFAULT_RANDOM_SLEEP_TIME = 200

        /**
         * 画图模式默认睡眠时间
         */
        private const val DEFAULT_DRAWING_SLEEP_TIME = 20
    }

    init {
        initData()
    }
}