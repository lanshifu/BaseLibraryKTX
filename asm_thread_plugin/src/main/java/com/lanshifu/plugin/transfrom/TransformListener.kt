package com.lanshifu.plugin.transfrom

/**
 * Represents the transform lifecycle listener
 *
 * @author johnsonlee
 */
interface TransformListener {

    fun onPreTransform(context: TransformContext) {}

    fun onPostTransform(context: TransformContext) {}

}