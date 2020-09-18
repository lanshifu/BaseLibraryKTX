package androidx.lifecycle

import android.annotation.SuppressLint
import com.lanshifu.lib.core.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

/**
 * @author lanxiaobin
 * @date 2020-04-30.
 *
 * 扩展函数，方便在Lifecycle组件中使用协程，
 *
 * 在 AppcompactActivity、Fragment 中使用自带生命周期管理的协程：
 *
 *
    lifecycleScope.launch {

        withContext(Dispatchers.Default) {
            logd("TestFragment 进入协程代码块:${Thread.currentThread()}")
        }

        logd("TestFragment,协程代码块完成:${Thread.currentThread()}")

        }
 *
 */


val LifecycleOwner.lifecycleScope: LifecycleCoroutineScope
    get() = lifecycle.lifecycleScope


/**
 * Lifecycle 内置了一个 mInternalScopeRef 变量来存储协程引用
 */
val Lifecycle.lifecycleScope: LifecycleCoroutineScope
    @SuppressLint("RestrictedApi")
    get() {
        while (true) {
            val existing = mInternalScopeRef.get() as LifecycleCoroutineScope?
            if (existing != null) {
                return existing
            }
            val newScope = LifecycleCoroutineScope(this)
            if (mInternalScopeRef.compareAndSet(null, newScope)) {
                return newScope
            }
        }
    }