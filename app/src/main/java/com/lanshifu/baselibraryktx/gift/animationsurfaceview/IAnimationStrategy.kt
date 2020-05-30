package com.lanshifu.baselibraryktx.gift.animationsurfaceview

import android.graphics.Canvas

interface IAnimationStrategy {
    fun compute()
    fun doing(): Boolean
    fun start()
    var x: Double
    var y: Double
    fun cancel()
    fun draw(canvas:Canvas)
}