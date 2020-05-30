package com.lanshifu.baselibraryktx.gift.animationsurfaceview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

/**
 * @author zhenghao.qi
 * @version 1.0
 * @time 2015年11月09日15:24:15
 */
class AnimationSurfaceView : SurfaceView, SurfaceHolder.Callback, Runnable {
    private var mSurfaceHolder: SurfaceHolder? = null
    /**
     * 获取要播放动画的bitmap
     */
    /**
     * 设置要播放动画的bitmap
     *
     * @param bitmap
     */
    var icon: Bitmap? = null
    private var mIAnimationStrategy: IAnimationStrategy? = null

    private var mStausChangedListener: OnStausChangedListener? = null //动画状态改变监听事件
    private var marginLeft = 0
    private var marginTop = 0
    private var isSurfaceDestoryed = true //默认未创建，相当于Destory
    private var mThread: Thread? = null

    private var animationList = mutableListOf<IAnimationStrategy>()

    public fun addAnimation(animation:IAnimationStrategy){
        animationList.add(animation)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        init()
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    //初始化
    private fun init() {
        mSurfaceHolder = holder
        mSurfaceHolder?.addCallback(this)
        setZOrderOnTop(true) //设置画布背景透明
        mSurfaceHolder?.setFormat(PixelFormat.TRANSPARENT)
        mThread = Thread(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        isSurfaceDestoryed = false
    }

    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int
    ) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isSurfaceDestoryed = true
        if (mIAnimationStrategy != null) //如果surfaceView创建后，没有执行setStrategy,就被销毁，会空指针异常
            mIAnimationStrategy!!.cancel()
    }

    //执行
    private fun executeAnimationStrategy() {
        var canvas: Canvas? = null
        val tempPaint = Paint()
        tempPaint.isAntiAlias = true
        tempPaint.color = Color.TRANSPARENT
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = Color.CYAN
        if (mStausChangedListener != null) {
            mStausChangedListener!!.onAnimationStart(this)
        }
        mIAnimationStrategy?.start()
        while (!isSurfaceDestoryed) {


            try {
                canvas = mSurfaceHolder!!.lockCanvas()

                canvas.drawColor(
                    Color.TRANSPARENT,
                    PorterDuff.Mode.CLEAR
                ) // 设置画布的背景为透明

//                mIAnimationStrategy?.compute()
//                // 绘上新图区域
//                val x = mIAnimationStrategy!!.x.toFloat() + marginLeft
//                val y = mIAnimationStrategy!!.y.toFloat() + marginTop
//                canvas.drawRect(x, y, x + icon!!.width, y + icon!!.height, tempPaint)
//                canvas.drawBitmap(icon!!, x, y, paint)

                animationList.forEach {
                    if (it.doing()){
                        it.draw(canvas!!)
                    }
                }


                mSurfaceHolder!!.unlockCanvasAndPost(canvas)
                Thread.sleep(REFRESH_INTERVAL_TIME)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // clear屏幕内容
        if (isSurfaceDestoryed == false) { // 如果直接按Home键回到桌面，这时候SurfaceView已经被销毁了，lockCanvas会返回为null。
            canvas = mSurfaceHolder!!.lockCanvas()
            canvas.drawColor(
                Color.TRANSPARENT,
                PorterDuff.Mode.CLEAR
            )
            mSurfaceHolder!!.unlockCanvasAndPost(canvas)
        }
        if (mStausChangedListener != null) {
            mStausChangedListener!!.onAnimationEnd(this)
        }
    }

    /**
     * 开始播放动画
     */
    fun startAnimation() {
        if (mThread!!.state == Thread.State.NEW) {
            mThread!!.start()
        } else if (mThread!!.state == Thread.State.TERMINATED) {
            mThread = Thread(this)
            mThread!!.start()
        }
    }

    /**
     * 是否正在播放动画
     */
    val isShow: Boolean
        get() = mIAnimationStrategy!!.doing()

    /**
     * 结束动画
     */
    fun endAnimation() {
        mIAnimationStrategy!!.cancel()
    }

    /**
     * 设置margin left 像素
     *
     * @param marginLeftPx
     */
    fun setMarginLeft(marginLeftPx: Int) {
        marginLeft = marginLeftPx
    }

    /**
     * 设置margin left 像素
     *
     * @param marginTopPx
     */
    fun setMarginTop(marginTopPx: Int) {
        marginTop = marginTopPx
    }

    /**
     * 设置动画状态改变监听器
     */
    fun setOnAnimationStausChangedListener(listener: OnStausChangedListener?) {
        mStausChangedListener = listener
    }

    override fun run() {
        executeAnimationStrategy()
    }

    interface OnStausChangedListener {
        fun onAnimationStart(view: AnimationSurfaceView?)
        fun onAnimationEnd(view: AnimationSurfaceView?)
    }

    /**
     * 设置动画执行算法策略
     *
     * @param strategy
     */
    fun setStrategy(strategy: IAnimationStrategy?) {
        mIAnimationStrategy = strategy
    }

    companion object {
        private const val TAG = "AnimationSurfaceView"
        private const val REFRESH_INTERVAL_TIME = 15L //每间隔15ms刷一帧
    }
}