package com.lanshifu.lib.core.lifecycle

import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*

/**
 * @author lanxiaobin
 * @date 2020-04-25
 *
 * 协程封装，结合Lifecycle，处理生命周期
 *
 * 默认在 onDestroy 取消任务
 *
 * @param lifecycleOwner AppcompatActivity、Fragment 都实现了 LifecycleOwner
 */
class LifecycleCoroutineScope(lifecycleOwner: LifecycleOwner) : BaseLifecycleObserver(lifecycleOwner) {


    private val coroutineScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)


    fun launch(block: suspend CoroutineScope.() -> Unit): Job {
        return launch(block, null)
    }


    /**
     * @param block     协程代码块，运行在UI线程
     * @param onError   异常回调，运行在UI线程
     * @param onStart   协程开始回调，运行在UI线程
     * @param onFinally 协程结束回调，不管成功/失败，都会回调，运行在UI线程
     */
    fun launch(
        block: suspend CoroutineScope.() -> Unit,
        onStart: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null,
        onFinally: (() -> Unit)? = null
    ): Job {
        return coroutineScope.launch {
            try {
                coroutineScope {
                    onStart?.invoke()
                    block()
                }
            } catch (e: Throwable) {
                if (onError != null) {
                    onError(e)
                } else {
                    e.printStackTrace()
                }
            } finally {
                onFinally?.invoke()
            }
        }
    }


    override fun onDestroy() {
        coroutineScope.cancel()
    }


    /**
     * 使用demo
     */
//    fun test(appCompatActivity: AppCompatActivity, fragment: Fragment) {
//
//        val log = LogApi("LifecycleCoroutineScope")
//
//        //简洁
//        LifecycleCoroutineScope(appCompatActivity).launch {
//
//            log.d("协程代码块，当前在主线程,: ${Thread.currentThread()}")
//            withContext(Dispatchers.Default) {
//                log.d("切换到子线程执行耗时操作,并挂起: ${Thread.currentThread()}")
//                Thread.sleep(5000)
//            }
//
//            log.d("耗时操作完成，回到主线程: ${Thread.currentThread()}")
//        }
//
//
//        /**
//         * 订制
//         */
//        LifecycleCoroutineScope(fragment).launch(
//                {
//                    log.d("协程代码块，当前在主线程,: ${Thread.currentThread()}")
//                    withContext(Dispatchers.Default) {
//                        log.d("切换到子线程执行耗时操作,并挂起: ${Thread.currentThread()}")
//                        Thread.sleep(5000)
//                    }
//                    log.d("耗时操作完成，回到主线程: ${Thread.currentThread()}")
//
//                },
//                onStart = {
//                    log.d("协程代码块之前调用")
//                },
//                onError = {
//                    log.d("异常处理")
//                },
//                onFinally = {
//                    log.d("finally 代码块")
//                })
//    }


}