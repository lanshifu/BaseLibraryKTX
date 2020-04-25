package com.lanshifu.lib.base

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lanshifu.lib.Ktx
import com.lanshifu.lib.core.util.Clazz
import com.lanshifu.lib.ext.logd
import com.lanshifu.lib.ext.toast
import com.lanshifu.lib.network.IBaseResponse
import com.lanshifu.lib.ext.isNetworkAvailable
import kotlinx.coroutines.*
import java.net.UnknownHostException

/**
通过泛型自动为 mRepository 赋值，不需要的话传Any即可
 */
open class BaseViewModel<T> : ViewModel(), LifecycleObserver {

    // 通过反射注入 mRepository
    val mRepository: T by lazy { Clazz.getClass<T>(this).newInstance() }

    val mException: MutableLiveData<Throwable> = MutableLiveData()


    private fun launchOnUI(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch { block() }
    }

    suspend fun <T> launchOnIO(block: suspend CoroutineScope.() -> T) {
        withContext(Dispatchers.IO) {
            block
        }
    }

    fun launch(tryBlock: suspend CoroutineScope.() -> Unit) {
        launchOnUI {
            tryCatch(tryBlock, {}, {}, true)
        }
    }


    fun launchOnUITryCatch(
        tryBlock: suspend CoroutineScope.() -> Unit,
        catchBlock: suspend CoroutineScope.(Throwable) -> Unit,
        finallyBlock: suspend CoroutineScope.() -> Unit,
        handleCancellationExceptionManually: Boolean = true
    ) {
        launchOnUI {
            tryCatch(tryBlock, catchBlock, finallyBlock, handleCancellationExceptionManually)
        }
    }

    private suspend fun tryCatch(
        tryBlock: suspend CoroutineScope.() -> Unit,
        catchBlock: suspend CoroutineScope.(Throwable) -> Unit,
        finallyBlock: suspend CoroutineScope.() -> Unit,
        handleCancellationExceptionManually: Boolean = false
    ) {
        coroutineScope {
            try {
                tryBlock()
            } catch (e: Throwable) {
                if (e !is CancellationException || handleCancellationExceptionManually) {
                    mException.value = e
                    catchBlock(e)
                } else {
                    throw e
                }
            } finally {
                finallyBlock()
            }
        }
    }

    fun launchUI(
        block: suspend CoroutineScope.() -> Unit,
        error: ((String) -> Unit)? = null
    ): Job {
        return viewModelScope.launch(Dispatchers.Main) {
            tryCatch(
                {
                    block()
                },
                {
                    "launchUI catch:$it".logd()
                    var err = it.message ?: "未知错误"
                    if (it is UnknownHostException) {
                        err = "网络错误"
                    }
                    error?.invoke(err) ?: toast(err)
                },
                {
                }, true
            )
        }
    }

    fun <R> quickLaunch(block: Execute<R>.() -> Unit) {
        Execute<R>().apply(block)
    }


    inner class Execute<R> {

        private var startBlock: (() -> Unit)? = null

        private var successBlock: ((R?) -> Unit)? = null
        private var successRespBlock: ((IBaseResponse<R>) -> Unit)? = null

        private var failBlock: ((String) -> Unit) = {
            logd("failBlock default:$it")
            toast(it)
        }

        fun onStart(block: () -> Unit) {
            this.startBlock = block
        }

        fun request(block: suspend CoroutineScope.() -> IBaseResponse<R>?) {
            startBlock?.invoke()
            launchUI(
                {
                    if (!Ktx.app.isNetworkAvailable()){
                        failBlock("网络连接失败")
                        return@launchUI
                    }

                    successBlock?.let {
                        block()?.execute(successBlock, failBlock)
                    } ?: block()?.executeResp(successRespBlock, failBlock)
                },
                failBlock
            )


        }

        fun onSuccess(block: (R?) -> Unit) {
            this.successBlock = block
        }

        fun onSuccessResp(block: (IBaseResponse<R>) -> Unit) {
            this.successRespBlock = block
        }

        fun onFail(block: (String) -> Unit) {
            this.failBlock = block
        }

    }
}