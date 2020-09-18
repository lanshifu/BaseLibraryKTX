package com.lanshifu.baselibraryktx.shell

import android.text.TextUtils
import android.util.Log
import com.jaredrummler.android.shell.Shell

/**
 * @author lanxiaobin
 * @date 2020/9/14.
 *
 * com.jaredrummler.android.shell
 */
object ShellTest {

    fun test(){
        val commands = "input touchscreen swipe 170 170 170 170 1"
        val commandResult = Shell.SU.run(commands)


        val stdout = commandResult.getStdout()
    }

    fun isAvailableByPing(ipORdomain: String): Boolean {
        if (TextUtils.isEmpty(ipORdomain)) {
            return false
        }

        Log.d("lxb", "isAvailableByPing,ping $ipORdomain")

        val commandResult = Shell.run(String.format("ping -c 1 %s", ipORdomain))

        Log.d("lxb", "isAvailableByPing,commandResult.exitCode=${commandResult.exitCode}")
        val ret = commandResult.isSuccessful
        if (ret) {
            Log.d("lxb", "isAvailableByPing() true")
        }else{
            Log.d("lxb", "isAvailableByPing() false")
        }
        return ret
    }
}