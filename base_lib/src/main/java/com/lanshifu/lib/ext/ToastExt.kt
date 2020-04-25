package com.lanshifu.lib.ext

import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes
import com.lanshifu.lib.Ktx

private var toast: Toast? = null

@JvmOverloads
fun showToast(content: String, duration: Int = Toast.LENGTH_SHORT) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        if (toast == null) {
            toast = Toast.makeText(Ktx.app, content, duration)
        } else {
            toast?.setText(content)
        }
        toast?.show()
    }
}

/**
 * 更方便
 */
fun Any.toast(content: String, duration: Int = Toast.LENGTH_SHORT) {
    showToast(content, duration)
}


fun Any.longToast(content: String, duration: Int = Toast.LENGTH_LONG) {
    showToast(content, duration)
}


