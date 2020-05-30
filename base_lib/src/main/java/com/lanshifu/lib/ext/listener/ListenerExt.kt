package com.lanshifu.lib.ext.listener

import android.animation.Animator
import android.view.animation.Animation
import com.airbnb.lottie.LottieAnimationView

/**
 * @author lanxiaobin
 * @date 2020/5/20.
 */

fun Animation.onAnimationListener(onAnimationRepeat: (animation: Animation?) -> Unit = {},
                                  onAnimationEnd: (animation: Animation?) -> Unit = {},
                                  onAnimationStart: (animation: Animation?) -> Unit = {}) {
    setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
            onAnimationRepeat.invoke(animation)
        }

        override fun onAnimationEnd(animation: Animation?) {
            onAnimationEnd.invoke(animation)
        }

        override fun onAnimationStart(animation: Animation?) {
            onAnimationStart.invoke(animation)
        }
    })
}

fun LottieAnimationView.onAnimatorListener(onAnimationRepeat: (animation: Animator?) -> Unit = {},
                                           onAnimationEnd: (animation: Animator?) -> Unit = {},
                                           onAnimationCancel: (animation: Animator?) -> Unit = {},
                                           onAnimationStart: (animation: Animator?) -> Unit = {}) {
    addAnimatorListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
            onAnimationRepeat.invoke(animation)
        }

        override fun onAnimationEnd(animation: Animator?) {
            onAnimationEnd.invoke(animation)
        }

        override fun onAnimationCancel(animation: Animator?) {
            onAnimationCancel.invoke(animation)
        }

        override fun onAnimationStart(animation: Animator?) {
            onAnimationStart.invoke(animation)
        }
    })
}

fun Animator.onAnimatorListener(onAnimationRepeat: (animation: Animator?) -> Unit = {},
                                onAnimationEnd: (animation: Animator?) -> Unit = {},
                                onAnimationCancel: (animation: Animator?) -> Unit = {},
                                onAnimationStart: (animation: Animator?) -> Unit = {}) {
    addListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
            onAnimationRepeat.invoke(animation)
        }

        override fun onAnimationEnd(animation: Animator?) {
            onAnimationEnd.invoke(animation)
        }

        override fun onAnimationCancel(animation: Animator?) {
            onAnimationCancel.invoke(animation)
        }

        override fun onAnimationStart(animation: Animator?) {
            onAnimationStart.invoke(animation)
        }
    })
}