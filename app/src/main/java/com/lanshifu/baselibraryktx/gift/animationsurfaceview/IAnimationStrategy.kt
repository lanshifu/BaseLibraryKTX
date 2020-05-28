package com.lanshifu.baselibraryktx.gift.animationsurfaceview

interface IAnimationStrategy {
    fun compute()
    fun doing(): Boolean
    fun start()
    var x: Double
    var y: Double
    fun cancel()
}