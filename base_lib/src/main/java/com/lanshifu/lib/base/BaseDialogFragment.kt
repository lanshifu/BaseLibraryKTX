package com.lanshifu.lib.base

import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import com.lanshifu.lib.ext.screenWidth


/**
 * @author lanxiaobin
 * @date 2019-12-30.
 */
open class BaseDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //不画任何东西，由onCreateView 决定布局
        setStyle(STYLE_NO_FRAME, 0)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = Dialog(activity, theme)
        dialog.setCanceledOnTouchOutside(isCancelable())
        dialog.setCancelable(isCancelable())
        dialog.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))//注意此处
            //强制设置宽高，不然状态栏被忽略
            it.setLayout(activity.screenWidth, activity.screenWidth)
            it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

            // 设置周边灰暗
            val layoutParams = dialog.window?.attributes
            layoutParams?.let {
                it.windowAnimations = android.R.style.Animation_Dialog
                it.alpha = 1f
                it.dimAmount = 0.4f
            }
            it.attributes = layoutParams
        }

        dialog.setOnKeyListener(object : DialogInterface.OnKeyListener {

            override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK && !isCancelable) {
                    return true
                }
                return false
            }
        })

        return dialog
    }


    override fun isCancelable(): Boolean {
        return false
    }

}