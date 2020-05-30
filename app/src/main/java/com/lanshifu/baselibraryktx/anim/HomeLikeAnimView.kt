package com.lanshifu.baselibraryktx.anim

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.lanshifu.baselibraryktx.R
import com.lanshifu.lib.ext.dp2px
import com.lanshifu.lib.ext.logd
import com.lanshifu.lib.ext.visible
import java.util.*


/**
 * 点赞动画
 *
 * todo 缓存
 */
class HomeLikeAnimView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    private var mLikeDrawables: MutableList<Drawable> = ArrayList()
    private var mDisLikeDrawables: MutableList<Drawable> = ArrayList()
    private var mRandom = Random()// 用于产生随机数,如生成随机图片

    private fun initParams() {
        mLikeDrawables.add(generateDrawable(R.mipmap.room_like_1))
        mLikeDrawables.add(generateDrawable(R.mipmap.room_like_2))
        mLikeDrawables.add(generateDrawable(R.mipmap.room_like_3))
        mLikeDrawables.add(generateDrawable(R.mipmap.room_like_4))
        mLikeDrawables.add(generateDrawable(R.mipmap.room_like_5))

        mDisLikeDrawables.add(generateDrawable(R.mipmap.room_dislike_1))
        mDisLikeDrawables.add(generateDrawable(R.mipmap.room_dislike_2))
        mDisLikeDrawables.add(generateDrawable(R.mipmap.room_dislike_3))
        mDisLikeDrawables.add(generateDrawable(R.mipmap.room_dislike_4))
        mDisLikeDrawables.add(generateDrawable(R.mipmap.room_dislike_5))
    }

    private val mCacheViewHolders = LinkedList<ViewHolder>()
    private val MAX_CACHE_SIZE = 0
    private var mTranslationY = 0f
    private var mTranslationX = 0f

    private val translationDistance = 350f

    private val childViewWidth = dp2px(80)
    private val childViewHeight = dp2px(80)

    private fun createViewHolder(): ViewHolder {
        val view = View.inflate(context, R.layout.room_like_float_view, null) as ViewGroup
        view.layoutParams = LayoutParams(childViewWidth, childViewHeight)
        val imageView = view.findViewById<ImageView>(R.id.ivLike)
        return ViewHolder(view, imageView)
    }

    private fun getViewHolder(): ViewHolder {
        var pop: ViewHolder? = null
        if (mCacheViewHolders.size > 0) {
            pop = mCacheViewHolders.pop()
        }
        pop?.let {
            logd("getImageView from cache,cache size=${mCacheViewHolders.size}")
            return it
        }

        return createViewHolder()
    }


    private fun generateDrawable(resID: Int): Drawable {
        return ContextCompat.getDrawable(context, resID)!!
    }

    /**
     * 动态添加 FlowView
     */
    fun addLikeView() {
        val viewHolder = getViewHolder()
        if (viewHolder.root.parent != null) {
            val parent = (viewHolder.root.parent) as ViewGroup
            parent.removeView(viewHolder.root)
        }
        viewHolder.imageView.setImageDrawable(mLikeDrawables[mRandom.nextInt(mLikeDrawables.size)])
        addView(viewHolder.root)
        startAnimation(viewHolder)
    }

    /**
     * 动态添加 FlowView
     */
    fun addDisLikeView() {
        val viewHolder = getViewHolder()
        if (viewHolder.root.parent != null) {
            val parent = (viewHolder.root.parent) as ViewGroup
            parent.removeView(viewHolder.root)
        }
        addView(viewHolder.root)
        startAnimation(viewHolder)
    }

    private fun startAnimation(viewHolder: ViewHolder) {
        val target = viewHolder.root
        val secondAnimator = generateSecondAnimation(target)
        val lastAnimator = generateLastAnimation(target)

        val finalAnimatorSet = AnimatorSet()
        finalAnimatorSet.setTarget(target)
//        finalAnimatorSet.playSequentially(secondAnimator)
        finalAnimatorSet.playSequentially(secondAnimator, lastAnimator)
        finalAnimatorSet.addListener(AnimationEndListener(viewHolder))

        lottieAnimationView?.cancelAnimation()
        lottieAnimationView?.visible()
        lottieAnimationView?.addAnimatorListener(object :
            android.animation.Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: android.animation.Animator?) {
            }

            override fun onAnimationEnd(animation: android.animation.Animator?) {
                finalAnimatorSet.start()
//                startUpAnim(imageView)
                lottieAnimationView?.removeAllAnimatorListeners()
            }

            override fun onAnimationCancel(animation: android.animation.Animator?) {
                finalAnimatorSet.start()
                lottieAnimationView?.removeAllAnimatorListeners()
            }

            override fun onAnimationStart(animation: android.animation.Animator?) {
            }

        })
        lottieAnimationView?.playAnimation()

        mTranslationY = 0f
        mTranslationX = 0f

    }

    private fun generateSecondAnimation(target: View): Animator {
        val translateY = ObjectAnimator.ofFloat(target, "translationY", 0f, -translationDistance)
        translateY.duration = 300
        val enterAnimation = AnimatorSet()
        enterAnimation.playTogether(translateY)
        enterAnimation.duration = 300
        enterAnimation.setTarget(target)
        return enterAnimation
    }


    private fun generateLastAnimation(target: View): Animator {
        //透明
        val alpha = ObjectAnimator.ofFloat(target, "alpha", 1f, 0f)
        val scaleX = ObjectAnimator.ofFloat(target, "scaleX", 1.0f, 0.5f)
        val scaleY = ObjectAnimator.ofFloat(target, "scaleY", 1.0f, 0.5f)
        //随机方向
        val maxDistance = 400
        val randomX = mRandom.nextInt(maxDistance) + 0f - mRandom.nextInt(maxDistance)

        val translateY = ObjectAnimator.ofFloat(
            target,
            "translationY",
            -translationDistance,
            -maxDistance - translationDistance
        )
        val randomTranslateX =
            ObjectAnimator.ofFloat(target, "translationX", mTranslationX, -randomX + mTranslationX)

        val lastAnimation = AnimatorSet()
        lastAnimation.playTogether(alpha, randomTranslateX, translateY, scaleX, scaleY)
        lastAnimation.duration = 500
        return lastAnimation
    }


    private inner class AnimationEndListener(private val viewHolder: ViewHolder) :
        AnimatorListenerAdapter() {

        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)

            val target = viewHolder.root

            removeView(target)
            if (target.parent != null) {
                val parent = (target.parent) as ViewGroup
                parent.removeView(target)
            }
            target.clearAnimation()

            if (mCacheViewHolders.size < MAX_CACHE_SIZE) {
                mCacheViewHolders.push(viewHolder)
                logd("onAnimationEnd,push to cache,cacheSize=${mCacheViewHolders.size}")
            }
        }

    }

    private fun recycle(target: ViewHolder) {
        target.imageView.clearAnimation()
        val root = target.root
        removeView(root)
        if (root != null) {
            val parent = (root) as ViewGroup
            parent.removeView(root)
        }
        target.imageView.setImageDrawable(null)

        if (mCacheViewHolders.size < MAX_CACHE_SIZE) {
            mCacheViewHolders.push(target)
            logd("onAnimationEnd,push to cache,cacheSize=${mCacheViewHolders.size}")
        }
    }

    override fun onDetachedFromWindow() {
        mCacheViewHolders.clear()
        super.onDetachedFromWindow()
    }

    init {
        initParams()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        addView(createLottieView())
    }


    var lottieAnimationView: LottieAnimationView? = null
    private fun createLottieView(): View? {
        var view = View.inflate(context, R.layout.room_like_lottieview, null)
        view.layoutParams = LayoutParams(childViewWidth, childViewHeight)
        lottieAnimationView = view.findViewById<LottieAnimationView>(R.id.lottieLikeView)
        return view
    }


    inner class ViewHolder(val root: ViewGroup, val imageView: ImageView)
}