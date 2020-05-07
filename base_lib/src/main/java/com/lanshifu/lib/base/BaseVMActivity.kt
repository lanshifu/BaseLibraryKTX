package com.lanshifu.lib.base

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.lanshifu.lib.core.util.Clazz


abstract class BaseVMActivity<VM : BaseViewModel<*>> : BaseActivity() {

    val mViewModel: VM by lazy { ViewModelProvider(this).get(Clazz.getClass<VM>(this)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startObserve()
    }

    open fun startObserve() {
        lifecycle.addObserver(mViewModel)
        mViewModel.mException.observe(this, Observer { it?.let { onError(it) } })
    }

    open fun onError(e: Throwable) {}

    override fun onDestroy() {
        mViewModel.let {
            lifecycle.removeObserver(it)
        }
        super.onDestroy()
    }
}