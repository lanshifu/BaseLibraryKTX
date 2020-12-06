package com.lanshifu.plugin;

import org.objectweb.asm.tree.ClassNode;

/**
 * @author lanxiaobin
 * @date 2020/11/28
 */
class ThreadHandleJava {
    ClassNode transform(ClassNode klass) {
        ThreadHandle threadHandle = new ThreadHandle();
        return threadHandle.transform(klass);
    }
}
