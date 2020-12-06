package com.lanshifu.plugin

import com.didiglobal.booster.kotlinx.asIterable
import com.didiglobal.booster.transform.asm.className
import com.didiglobal.booster.transform.asm.find
import com.didiglobal.booster.transform.asm.isInstanceOf
import com.didiglobal.booster.transform.thread.*
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import javax.xml.crypto.dsig.TransformException

/**
 * @author lanxiaobin
 * @date 2020/11/28
 */
public class ThreadHandle {
    
    val optimizationEnabled = true
    fun transform(klass: ClassNode): ClassNode {

        klass.methods?.forEach { method ->
            //每个方法的字节码遍历
            method.instructions?.iterator()?.asIterable()?.forEach {

                when (it.opcode) {
                    //在线程相关的方法调用前设置名称
//                    Opcodes.INVOKEVIRTUAL -> (it as MethodInsnNode).transformInvokeVirtual(klass, method)
//                    Opcodes.INVOKESTATIC -> (it as MethodInsnNode).transformInvokeStatic(klass, method)
//                    Opcodes.INVOKESPECIAL -> (it as MethodInsnNode).transformInvokeSpecial(klass, method)
                    // 遇到NEW指令，将 Thread 的构造方法调用替换成对应的ShadowThread的构造方法
                    Opcodes.NEW -> (it as TypeInsnNode).transform( klass, method)
                    // ARETURN，A对应对象，表示返回对象指令，例如 new Thread()
                    Opcodes.ARETURN -> if (method.desc == "L$THREAD;") {
                        //return 之前插入字节码，调用setThreadName方法
                        //1.将 String压栈
                        method.instructions.insertBefore(it, LdcInsnNode(
                            makeThreadName(
                                klass.className
                            )
                        )
                        )
                        //2.调用 ShadowThread 的setThreadName方法，参数就是String
                        method.instructions.insertBefore(it, MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            SHADOW_THREAD, "setThreadName", "(Ljava/lang/Thread;Ljava/lang/String;)Ljava/lang/Thread;", false)
                        )

                    }
                }
            }
        }
        return klass
    }

    private fun TypeInsnNode.transform(klass: ClassNode, method: MethodNode) {
        when (this.desc) {
            /*-*/ HANDLER_THREAD -> this.transformNew(klass, method,
            SHADOW_HANDLER_THREAD
        )
            /*---------*/ THREAD -> this.transformNew(klass, method,
            SHADOW_THREAD
        )
            THREAD_POOL_EXECUTOR -> this.transformNew(klass, method,
                SHADOW_THREAD_POOL_EXECUTOR, true)
            /*----------*/ TIMER -> this.transformNew(klass, method,
            SHADOW_TIMER
        )
        }
    }

    private fun TypeInsnNode.transformNew(klass: ClassNode, method: MethodNode, type: String, optimizable: Boolean = false) {
        this.find {
            (it.opcode == Opcodes.INVOKESPECIAL) &&
                    (it is MethodInsnNode) &&
                    (this.desc == it.owner && "<init>" == it.name)
        }?.isInstanceOf { init: MethodInsnNode ->
            // replace original type with shadowed type
            // e.g. android/os/HandlerThread => com/didiglobal/booster/instrument/ShadowHandlerThread
            this.desc = type

            //替换构造，相当于创建Thread的子类 ShadowThread，构造增加一个String参数
            // replace the constructor of original type with the constructor of shadowed type
            // e.g. android/os/HandlerThread(Ljava/lang/String;) => com/didiglobal/booster/instrument/ShadowHandlerThread(Ljava/lang/String;Ljava/lang/String;)
            val rp = init.desc.lastIndexOf(')')
            init.apply {
                //替换owner，也就是把Thread替换成ShadowThread
                owner = type
                //方法描述要增加一个String参数
                desc = "${desc.substring(0, rp)}Ljava/lang/String;${if (optimizable) "Z" else ""}${desc.substring(rp)}"
            }

            //增加一个参数，将线程名压栈
            method.instructions.insertBefore(init, LdcInsnNode(
                makeThreadName(
                    klass.className
                )
            ))
            //调用构造
            if (optimizable) {
                method.instructions.insertBefore(init, LdcInsnNode(optimizationEnabled))
            }
            //如果是false呢？？？
        } ?: throw TransformException("`invokespecial $desc` not found: ${klass.name}.${method.name}${method.desc}")
    }

    private fun makeThreadName(name: String) = "$MARK for lanshifu-$name"
}