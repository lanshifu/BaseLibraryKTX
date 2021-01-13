package com.lanshifu.baselibraryktx

import android.graphics.Bitmap
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lanshifu.lib.base.BaseViewModel
import io.alterac.blurkit.BlurKit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @author lanxiaobin
 * @date 2020/9/20
 */
class MainVM : BaseViewModel<Any>() {


    var smsCode = MutableLiveData<String>()
    var blurBitmap = MutableLiveData<Bitmap>()

    fun getSmsCode(phone: String) {

        viewModelScope.launch {
            flow {
                (60 downTo 0).forEach {
                    delay(1000)
                    emit("$it s")
                }
            }.flowOn(Dispatchers.Default)
                .onStart {
                    // 倒计时开始 ，在这里可以让Button 禁止点击状态
                }
                .onCompletion {
                    // 倒计时结束 ，在这里可以让Button 恢复点击状态
                }
                .collect {
                    // 在这里 更新LiveData 的值来显示到UI
                    smsCode.value = it
                }
        }
    }

    fun blur(blurView:View){
        viewModelScope.launch(Dispatchers.Default) {
            var bitmap = BlurKit.getInstance().blur(blurView,25)
            if (bitmap != null) {
                withContext(Dispatchers.Main) {
                    blurBitmap.value = bitmap
                }
            }
        }
    }
}