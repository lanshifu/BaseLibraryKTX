package com.didiglobal.booster.transform.thread

import com.didiglobal.booster.kotlinx.Build
import com.didiglobal.booster.kotlinx.asIterable
import com.didiglobal.booster.kotlinx.file
import com.didiglobal.booster.kotlinx.touch
import com.didiglobal.booster.transform.ArtifactManager
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.*
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import java.io.PrintWriter
import javax.xml.crypto.dsig.TransformException
import javax.xml.parsers.SAXParserFactory

/**
 * Represents a class transformer for multithreading optimization
 *
 * @author johnsonlee
 */
//@AutoService(ClassTransformer::class)
class ThreadTransformer : ClassTransformer {

    private lateinit var logger: PrintWriter
    private var optimizationEnabled = true
    private val applications = mutableSetOf<String>()

    override fun onPreTransform(context: TransformContext) {
        //解析manifest文件，保存Application、和四大组件,为了过滤用
        val parser = SAXParserFactory.newInstance().newSAXParser()
        context.artifacts.get(ArtifactManager.MERGED_MANIFESTS).forEach { manifest ->
            val handler = ComponentHandler()
            parser.parse(manifest, handler)
            applications.addAll(handler.applications)
        }
//        this.optimizationEnabled = context.getProperty(PROPERTY_OPTIMIZATION_ENABLED, "$DEFAULT_OPTIMIZATION_ENABLED").toBoolean()
        this.optimizationEnabled = true
        this.logger = context.reportsDir.file(Build.ARTIFACT).file(context.name).file("report.txt").touch().printWriter()

        logger.println("applications size = ${applications.size}")
    }

    override fun onPostTransform(context: TransformContext) {
        this.logger.close()
    }

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        if (klass.name.startsWith(BOOSTER_INSTRUMENT)) {
            // ignore booster instrumented classes
            return klass
        }

        //在 Application 类的 <clinit>() 中调用 ShadowAsyncTask.optimizeAsyncTaskExecutor() 修改 AsyncTask 的线程池参数
        if (this.applications.contains(klass.className)) {
            optimizeAsyncTask(klass)
        }

        klass.methods?.forEach { method ->
            //每个方法的字节码遍历
            method.instructions?.iterator()?.asIterable()?.forEach {

/**
    L2
    LINENUMBER 17 L2
    NEW java/lang/Thread
    DUP
    GETSTATIC com/lanshifu/baselibraryktx/threadtest/ThreadTest$run${'$'}1.INSTANCE : Lcom/lanshifu/baselibraryktx/threadtest/ThreadTest$run${'$'}1;
    CHECKCAST java/lang/Runnable
    INVOKESPECIAL java/lang/Thread.<init> (Ljava/lang/Runnable;)V
**/

                when (it.opcode) {
                    //在线程相关的方法调用前设置名称
                    Opcodes.INVOKEVIRTUAL -> (it as MethodInsnNode).transformInvokeVirtual(context, klass, method)
                    Opcodes.INVOKESTATIC -> (it as MethodInsnNode).transformInvokeStatic(context, klass, method)
                    Opcodes.INVOKESPECIAL -> (it as MethodInsnNode).transformInvokeSpecial(context, klass, method)
                    // 遇到NEW指令，将 Thread 的构造方法调用替换成对应的ShadowThread的构造方法
                    Opcodes.NEW -> (it as TypeInsnNode).transform(context, klass, method)
                    // ARETURN，A对应对象，表示返回对象指令，例如 new Thread()
                    Opcodes.ARETURN -> if (method.desc == "L$THREAD;") {
                        //return 之前插入字节码，调用setThreadName方法
                        //1.将 String压栈
                        method.instructions.insertBefore(it, LdcInsnNode(
                            makeThreadName(
                                klass.className
                            )
                        ))
                        //2.调用 ShadowThread 的setThreadName方法，参数就是String
                        method.instructions.insertBefore(it, MethodInsnNode(Opcodes.INVOKESTATIC,
                            SHADOW_THREAD, "setThreadName", "(Ljava/lang/Thread;Ljava/lang/String;)Ljava/lang/Thread;", false))

                        logger.println("ARETURN + $SHADOW_THREAD.makeThreadName(Ljava/lang/String;Ljava/lang/String;): ${klass.name}.${method.name}${method.desc}")
                    }
                }
            }
        }
        return klass
    }

    private fun MethodInsnNode.transformInvokeVirtual(context: TransformContext, klass: ClassNode, method: MethodNode) {
        if (context.klassPool.get(THREAD).isAssignableFrom(this.owner)) {
            when ("${this.name}${this.desc}") {
                "start()V" -> {
                    method.instructions.insertBefore(this, LdcInsnNode(
                        makeThreadName(
                            klass.className
                        )
                    ))
                    method.instructions.insertBefore(this, MethodInsnNode(Opcodes.INVOKESTATIC,
                        SHADOW_THREAD, "setThreadName", "(Ljava/lang/Thread;Ljava/lang/String;)Ljava/lang/Thread;", false))
                    logger.println("start + $SHADOW_THREAD.makeThreadName(Ljava/lang/String;Ljava/lang/String;) => ${this.owner}.${this.name}${this.desc}: ${klass.name}.${method.name}${method.desc}")
                    this.owner = THREAD
                }
                "setName(Ljava/lang/String;)V" -> {
                    method.instructions.insertBefore(this, LdcInsnNode(
                        makeThreadName(
                            klass.className
                        )
                    ))
                    method.instructions.insertBefore(this, MethodInsnNode(Opcodes.INVOKESTATIC,
                        SHADOW_THREAD, "makeThreadName", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false))
                    logger.println("setName + $SHADOW_THREAD.makeThreadName(Ljava/lang/String;Ljava/lang/String;) => ${this.owner}.${this.name}${this.desc}: ${klass.name}.${method.name}${method.desc}")
                    this.owner = THREAD
                }
            }
        }
    }

    /**
     * Transform the `super` call of the following classes:
     *
     * - `java.lang.Thread`
     * - `java.util.Timer`
     * - `java.util.concurrent.ThreadPoolExecutor`
     * - `android.os.HandlerThread`
     */
    private fun MethodInsnNode.transformInvokeSpecial(@Suppress("UNUSED_PARAMETER") context: TransformContext, klass: ClassNode, method: MethodNode) {
        if (this.name != "<init>") {
            return
        }
        when (this.owner) {
            THREAD -> transformThreadInvokeSpecial(klass, method)
            HANDLER_THREAD -> transformHandlerThreadInvokeSpecial(klass, method)
            TIMER -> transformTimerInvokeSpecial(klass, method)
            THREAD_POOL_EXECUTOR -> transformThreadPoolExecutorInvokeSpecial(klass, method)
        }
    }

    private fun MethodInsnNode.transformThreadPoolExecutorInvokeSpecial(klass: ClassNode, method: MethodNode, init: MethodInsnNode = this) {
        when (this.desc) {
            // ThreadPoolExecutor(int, int, long, TimeUnit, BlockingQueue)
            "(IIJLjava/util/concurrent/TimeUnit;Ljava/util/concurrent/BlockingQueue;)V" -> {
                method.instructions.apply {
                    // ..., queue => ..., queue, prefix
                    insertBefore(init, LdcInsnNode(
                        makeThreadName(
                            klass.className
                        )
                    ))
                    // ..., queue, prefix => ..., queue, factory
                    insertBefore(init, MethodInsnNode(Opcodes.INVOKESTATIC,
                        NAMED_THREAD_FACTORY, "newInstance", "(Ljava/lang/String;)Ljava/util/concurrent/ThreadFactory;", false))
                }
                this.desc = "(IIJLjava/util/concurrent/TimeUnit;Ljava/util/concurrent/BlockingQueue;Ljava/util/concurrent/ThreadFactory;)V"
            }
            // ThreadPoolExecutor(int, int, long, TimeUnit, BlockingQueue, ThreadFactory)
            "(IIJLjava/util/concurrent/TimeUnit;Ljava/util/concurrent/BlockingQueue;Ljava/util/concurrent/ThreadFactory;)V" -> {
                method.instructions.apply {
                    // ..., factory => ..., factory, prefix
                    insertBefore(init, LdcInsnNode(
                        makeThreadName(
                            klass.className
                        )
                    ))
                    // ..., factory, prefix => ..., factory
                    insertBefore(init, MethodInsnNode(Opcodes.INVOKESTATIC,
                        NAMED_THREAD_FACTORY, "newInstance", "(Ljava/util/concurrent/ThreadFactory;Ljava/lang/String;)Ljava/util/concurrent/ThreadFactory;"))
                }
            }
            // ThreadPoolExecutor(int, int, long, TimeUnit, BlockingQueue, RejectedExecutionHandler)
            "(IIJLjava/util/concurrent/TimeUnit;Ljava/util/concurrent/BlockingQueue;Ljava/util/concurrent/RejectedExecutionHandler;)V" -> {
                method.instructions.apply {
                    // ..., handler => ..., handler, prefix
                    insertBefore(init, LdcInsnNode(
                        makeThreadName(
                            klass.className
                        )
                    ))
                    // ..., handler, prefix => ..., handler, factory
                    insertBefore(init, MethodInsnNode(Opcodes.INVOKESTATIC,
                        NAMED_THREAD_FACTORY, "newInstance", "(Ljava/lang/String;)Ljava/util/concurrent/ThreadFactory;", false))
                    // ..., handler, factory => ..., factory, handler
                    insertBefore(init, InsnNode(Opcodes.SWAP))
                }
                this.desc = "(IIJLjava/util/concurrent/TimeUnit;Ljava/util/concurrent/BlockingQueue;Ljava/util/concurrent/ThreadFactory;Ljava/util/concurrent/RejectedExecutionHandler;)V"
            }
            // ThreadPoolExecutor(int, int, long, TimeUnit, BlockingQueue, ThreadFactory, RejectedExecutionHandler)
            "(IIJLjava/util/concurrent/TimeUnit;Ljava/util/concurrent/BlockingQueue;Ljava/util/concurrent/ThreadFactory;Ljava/util/concurrent/RejectedExecutionHandler;)V" -> {
                method.instructions.apply {
                    // ..., factory, handler => ..., handler, factory
                    insertBefore(init, InsnNode(Opcodes.SWAP))
                    // ..., handler, factory => ..., handler, factory, prefix
                    insertBefore(init, LdcInsnNode(
                        makeThreadName(
                            klass.className
                        )
                    ))
                    // ..., handler, factory, prefix => ..., handler, factory
                    insertBefore(init, MethodInsnNode(Opcodes.INVOKESTATIC,
                        NAMED_THREAD_FACTORY, "newInstance", "(Ljava/util/concurrent/ThreadFactory;Ljava/lang/String;)Ljava/util/concurrent/ThreadFactory;"))
                    // ..., handler, factory => ..., factory, handler
                    insertBefore(init, InsnNode(Opcodes.SWAP))
                }
            }
        }
    }

    private fun MethodInsnNode.transformTimerInvokeSpecial(klass: ClassNode, method: MethodNode, init: MethodInsnNode = this) {
        when (this.desc) {
            // Timer()
            "()V" -> {
                method.instructions.insertBefore(init, LdcInsnNode(
                    makeThreadName(
                        klass.className
                    )
                ))
                this.desc = "(Ljava/lang/String;)V"
            }
            // Timer(boolean)
            "(Z)V" -> {
                method.instructions.apply {
                    // ..., isDaemon => ..., isDaemon, name
                    insertBefore(init, LdcInsnNode(
                        makeThreadName(
                            klass.className
                        )
                    ))
                    // ..., isDaemon, name => ..., name, isDaemon
                    insertBefore(init, InsnNode(Opcodes.SWAP))
                }
                this.desc = "(Ljava/lang/String;Z)V"
            }
            // Timer(String)
            "(Ljava/lang/String;)V" -> {
                method.instructions.apply {
                    // ..., name => ..., name, prefix
                    insertBefore(init, LdcInsnNode(
                        makeThreadName(
                            klass.className
                        )
                    ))
                    // ..., name, prefix => ..., name
                    insertBefore(init, MethodInsnNode(Opcodes.INVOKESTATIC,
                        SHADOW_THREAD, "makeThreadName", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false))
                }
            }
            // Timer(String, boolean)
            "(Ljava/lang/String;Z)V" -> {
                method.instructions.apply {
                    // ..., name, isDaemon => ..., isDaemon, name
                    insertBefore(init, InsnNode(Opcodes.SWAP))
                    // ..., isDaemon, name => ..., isDaemon, name, prefix
                    insertBefore(init, LdcInsnNode(
                        makeThreadName(
                            klass.className
                        )
                    ))
                    // ..., isDaemon, name, prefix => ..., isDaemon, name
                    insertBefore(init, MethodInsnNode(Opcodes.INVOKESTATIC,
                        SHADOW_THREAD, "makeThreadName", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false))
                    // ..., isDaemon, name => ..., name, isDaemon
                    insertBefore(init, InsnNode(Opcodes.SWAP))
                }
            }
        }
    }

    private fun MethodInsnNode.transformHandlerThreadInvokeSpecial(klass: ClassNode, method: MethodNode, init: MethodInsnNode = this) {
        when (this.desc) {
            // HandlerThread(String)
            "(Ljava/lang/String;)V" -> {
                method.instructions.apply {
                    insertBefore(init, LdcInsnNode(
                        makeThreadName(
                            klass.className
                        )
                    ))
                    insertBefore(init, MethodInsnNode(Opcodes.INVOKESTATIC,
                        SHADOW_THREAD, "makeThreadName", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false))
                }
                logger.println("transformHandlerThreadInvokeSpecial + $SHADOW_THREAD.makeThreadName(Ljava/lang/String;Ljava/lang/String;) => ${owner}.${name}${desc}: ${klass.name}.${method.name}${method.desc}")
            }
            // HandlerThread(String, int)
            "(Ljava/lang/String;I)V" -> {
                method.instructions.apply {
                    // ..., name, priority => ..., priority, name
                    insertBefore(init, InsnNode(Opcodes.SWAP))
                    // ..., priority, name => ..., priority, name, prefix
                    insertBefore(init, LdcInsnNode(
                        makeThreadName(
                            klass.className
                        )
                    ))
                    // ..., priority, name, prefix => ..., priority, name
                    insertBefore(init, MethodInsnNode(Opcodes.INVOKESTATIC,
                        SHADOW_THREAD, "makeThreadName", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false))
                    // ..., priority, name => ..., name, priority
                    insertBefore(init, InsnNode(Opcodes.SWAP))
                }
                logger.println("transformHandlerThreadInvokeSpecial + $SHADOW_THREAD.makeThreadName(Ljava/lang/String;Ljava/lang/String;) => ${owner}.${name}${desc}: ${klass.name}.${method.name}${method.desc}")
            }
        }
    }

    private fun MethodInsnNode.transformThreadInvokeSpecial(klass: ClassNode, method: MethodNode, init: MethodInsnNode = this) {
        when (this.desc) {
            // Thread()
            "()V",
            // Thread(Runnable)
            "(Ljava/lang/Runnable;)V",
            // Thread(ThreaGroup, Runnable)
            "(Ljava/lang/ThreadGroup;Ljava/lang/Runnable;)V" -> {
                method.instructions.insertBefore(this, LdcInsnNode(
                    makeThreadName(
                        klass.className
                    )
                ))
                val r = this.desc.lastIndexOf(')')
                val desc = "${this.desc.substring(0, r)}Ljava/lang/String;${this.desc.substring(r)}"
                logger.println("transformThreadInvokeSpecial + $SHADOW_THREAD.makeThreadName(Ljava/lang/String;Ljava/lang/String;) => ${this.owner}.${this.name}${this.desc}: ${klass.name}.${method.name}${method.desc}")
                logger.println("transformThreadInvokeSpecial * ${this.owner}.${this.name}${this.desc} => ${this.owner}.${this.name}$desc: ${klass.name}.${method.name}${method.desc}")
                this.desc = desc
            }
            // Thread(String)
            "(Ljava/lang/String;)V",
            // Thread(ThreadGroup, String)
            "(Ljava/lang/ThreadGroup;Ljava/lang/String;)V",
            // Thread(Runnable, String)
            "(Ljava/lang/Runnable;Ljava/lang/String;)V",
            // Thread(ThreadGroup, Runnable, String)
            "(Ljava/lang/ThreadGroup;Ljava/lang/Runnable;Ljava/lang/String;)V" -> {
                method.instructions.apply {
                    insertBefore(init, LdcInsnNode(
                        makeThreadName(
                            klass.className
                        )
                    ))
                    insertBefore(init, MethodInsnNode(Opcodes.INVOKESTATIC,
                        SHADOW_THREAD, "makeThreadName", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false))
                }
                logger.println("transformThreadInvokeSpecial + $SHADOW_THREAD.makeThreadName(Ljava/lang/String;Ljava/lang/String;) => ${this.owner}.${this.name}${this.desc}: ${klass.name}.${method.name}${method.desc}")
            }
            // Thread(ThreadGroup, Runnable, String, long)
            "(Ljava/lang/ThreadGroup;Ljava/lang/Runnable;Ljava/lang/String;J)V" -> {
                // in order to modify the thread name, the penultimate argument `name` have to be moved on the top
                // of operand stack, so that the `ShadowThread.makeThreadName(String, String)` could be invoked to
                // consume the `name` on the top of operand stack, and then a new name returned on the top of
                // operand stack.
                // due to JVM does not support swap long/double on the top of operand stack, so, we have to combine
                // DUP* and POP* to swap `name` and `stackSize`
                method.instructions.apply {
                    // ..., name, stackSize => ..., stackSize, name, stackSize
                    insertBefore(init, InsnNode(Opcodes.DUP2_X1))
                    // ..., stackSize, name, stackSize => ..., stackSize, name
                    insertBefore(init, InsnNode(Opcodes.POP2))
                    // ..., stackSize, name => ..., stackSize, name, prefix
                    insertBefore(init, LdcInsnNode(
                        makeThreadName(
                            klass.className
                        )
                    ))
                    // ..., stackSize, name, prefix => ..., stackSize, name
                    insertBefore(init, MethodInsnNode(Opcodes.INVOKESTATIC,
                        SHADOW_THREAD, "makeThreadName", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false))
                    // ..., stackSize, name => ..., stackSize, name, name
                    insertBefore(init, InsnNode(Opcodes.DUP))
                    // ..., stackSize, name, name => ..., name, name, stackSize, name, name
                    insertBefore(init, InsnNode(Opcodes.DUP2_X2))
                    // ..., name, name, stackSize, name, name => ..., name, name, stackSize
                    insertBefore(init, InsnNode(Opcodes.POP2))
                    // ..., name, name, stackSize => ..., name, stackSize, name, stackSize
                    insertBefore(init, InsnNode(Opcodes.DUP2_X1))
                    // ..., name, stackSize, name, stackSize => ..., name, stackSize, name
                    insertBefore(init, InsnNode(Opcodes.POP2))
                    // ..., name, stackSize, name => ..., name, stackSize
                    insertBefore(init, InsnNode(Opcodes.POP))
                }
                logger.println("transformThreadInvokeSpecial + $SHADOW_THREAD.makeThreadName(Ljava/lang/String;Ljava/lang/String;) => ${this.owner}.${this.name}${this.desc}: ${klass.name}.${method.name}${method.desc}")
            }
        }
    }

    /**
     * Transform the static calls of [java.util.concurrent.Executors]
     */
    private fun MethodInsnNode.transformInvokeStatic(@Suppress("UNUSED_PARAMETER") context: TransformContext, klass: ClassNode, method: MethodNode) {
        when (this.owner) {
            EXECUTORS -> {
                when (this.name) {
                    "defaultThreadFactory" -> {
                        val r = this.desc.lastIndexOf(')')
                        val desc = "${this.desc.substring(0, r)}Ljava/lang/String;${this.desc.substring(r)}"
                        logger.println("${this.name} * ${this.owner}.${this.name}${this.desc} => $SHADOW_EXECUTORS.${this.name}$desc: ${klass.name}.${method.name}${method.desc}")
                        this.owner =
                            SHADOW_EXECUTORS
                        this.desc = desc
                        method.instructions.insertBefore(this, LdcInsnNode(
                            makeThreadName(
                                klass.className
                            )
                        ))
                    }
                    "newCachedThreadPool",
                    "newFixedThreadPool",
                    "newSingleThreadExecutor",
                    "newSingleThreadScheduledExecutor",
                    "newScheduledThreadPool",
                    "newWorkStealingPool"-> {
                        val r = this.desc.lastIndexOf(')')
                        val name = if (optimizationEnabled) this.name.replace("new", "newOptimized") else this.name
                        val desc = "${this.desc.substring(0, r)}Ljava/lang/String;${this.desc.substring(r)}"
                        logger.println("${this.name} * ${this.owner}.${this.name}${this.desc} => $SHADOW_EXECUTORS.$name$desc: ${klass.name}.${method.name}${method.desc}")
                        this.owner =
                            SHADOW_EXECUTORS
                        this.name = name
                        this.desc = desc
                        method.instructions.insertBefore(this, LdcInsnNode(
                            makeThreadName(
                                klass.className
                            )
                        ))
                    }
                }
            }
        }

    }

    private fun TypeInsnNode.transform(context: TransformContext, klass: ClassNode, method: MethodNode) {
        when (this.desc) {
            /*-*/ HANDLER_THREAD -> this.transformNew(context, klass, method,
            SHADOW_HANDLER_THREAD
        )
            /*---------*/ THREAD -> this.transformNew(context, klass, method,
            SHADOW_THREAD
        )
            THREAD_POOL_EXECUTOR -> this.transformNew(context, klass, method,
                SHADOW_THREAD_POOL_EXECUTOR, true)
            /*----------*/ TIMER -> this.transformNew(context, klass, method,
            SHADOW_TIMER
        )
        }
    }

    private fun TypeInsnNode.transformNew(@Suppress("UNUSED_PARAMETER") context: TransformContext, klass: ClassNode, method: MethodNode, type: String, optimizable: Boolean = false) {
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

    private fun optimizeAsyncTask(klass: ClassNode) {
        val method = klass.methods?.find {
            "${it.name}${it.desc}" == "<clinit>()V"
        } ?: klass.defaultClinit.also {
            klass.methods.add(it)
        }

        method.instructions?.let { insn ->
            insn.findAll(Opcodes.RETURN, Opcodes.ATHROW).forEach {
                insn.insertBefore(it, MethodInsnNode(Opcodes.INVOKESTATIC,
                    SHADOW_ASYNC_TASK, "optimizeAsyncTaskExecutor", "()V", false))
                logger.println(" + $SHADOW_ASYNC_TASK.optimizeAsyncTaskExecutor()V: ${klass.name}.${method.name}${method.desc} ")

            }
        }
    }

}

private fun makeThreadName(name: String) = "$MARK lizhi-$name"

private val ClassNode.defaultClinit: MethodNode
    get() = MethodNode(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null).apply {
        maxStack = 1
        instructions.add(InsnNode(Opcodes.RETURN))
    }


internal const val MARK = "\u200B"

internal const val BOOSTER_INSTRUMENT = "com/lanshifu/asm_plugin_library/thread/"
//internal const val BOOSTER_INSTRUMENT = "com/didiglobal/booster/instrument/"
internal const val SHADOW = "${BOOSTER_INSTRUMENT}Shadow"
internal const val SHADOW_HANDLER_THREAD = "${SHADOW}HandlerThread"
internal const val SHADOW_THREAD = "${SHADOW}Thread"
internal const val SHADOW_TIMER = "${SHADOW}Timer"
internal const val SHADOW_EXECUTORS = "${SHADOW}Executors"
internal const val SHADOW_THREAD_POOL_EXECUTOR = "${SHADOW}ThreadPoolExecutor"
internal const val SHADOW_ASYNC_TASK = "${SHADOW}AsyncTask"
internal const val NAMED_THREAD_FACTORY = "${BOOSTER_INSTRUMENT}NamedThreadFactory"


internal const val JAVA_UTIL = "java/util/"
internal const val JAVA_UTIL_CONCURRENT = "${JAVA_UTIL}concurrent/"
internal const val HANDLER_THREAD = "android/os/HandlerThread"
internal const val THREAD = "java/lang/Thread"
internal const val TIMER = "${JAVA_UTIL}Timer"
internal const val EXECUTORS = "${JAVA_UTIL_CONCURRENT}Executors"
internal const val THREAD_POOL_EXECUTOR = "${JAVA_UTIL_CONCURRENT}ThreadPoolExecutor"
