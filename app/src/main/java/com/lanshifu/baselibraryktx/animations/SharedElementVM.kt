package com.lanshifu.baselibraryktx.animations

import androidx.lifecycle.MutableLiveData
import com.lanshifu.lib.base.BaseViewModel
import com.lanshifu.lib.ext.logd
import com.lanshifu.lib.ext.logi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

/**
 * @author lanxiaobin
 * @date 2020/12/6
 */
class SharedElementVM : BaseViewModel<Any>() {

    val titleUpdateResult = MutableLiveData<String>()

    val progressUpdateResult = MutableLiveData<Int>()


    fun startPlay(){

        logi("startPlay")

        launch {

            withContext(Dispatchers.Main){
                titleUpdateResult.value = "更新了标题啦啦啦"
            }

            withContext(Dispatchers.Default){
                flow {
                    (60 downTo 0).forEach {
                        delay(1000)
                        emit(it)
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
                        withContext(Dispatchers.Main){
                            progressUpdateResult.value = it
                            logd("progressUpdateResult:$it")
                        }
                    }
            }

        }
    }


    var index = 1
    fun next(){
        titleUpdateResult.value = titleUpdateResult.value + ":${++index}"
    }

    fun pre(){
        titleUpdateResult.value = titleUpdateResult.value + ":${--index}"
    }
}