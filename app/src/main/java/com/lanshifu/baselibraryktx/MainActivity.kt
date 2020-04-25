package com.lanshifu.baselibraryktx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lanshifu.lib.core.lifecycle.LifecycleCoroutineScope
import com.lanshifu.lib.ext.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        LifecycleCoroutineScope(this)
            .launch {
                withContext(Dispatchers.Default){
                    Thread.sleep(3000)
                }

                toast("协程代码块执行完成")
            }
    }
}
