package com.lanshifu.plugin.transfrom

import org.objectweb.asm.tree.ClassNode

/**
 * Represents class transformer
 *
 * @author johnsonlee
 */
interface ClassTransformer : TransformListener {

    /**
     * Transform the specified class node
     *
     * @param context The transform context
     * @param klass The class node to be transformed
     * @return The transformed class node
     */
    fun transform(context: TransformContext, klass: ClassNode) = klass

}
