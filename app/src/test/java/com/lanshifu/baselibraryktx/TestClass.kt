package com.lanshifu.baselibraryktx

import java.text.DecimalFormat

/**
 * @author lanxiaobin
 * @date 2020/8/19.
 */
object TestClass {

    @JvmStatic
    fun textForNumber(number:Long) : String {
        if (number < 1000) return number.toString()
        val df = DecimalFormat("000")//保留3位数
        if (number < 1000 * 1000) return "${(number / 1000)},${df.format((number % 1000f))}"

        val buf = StringBuffer()
        var n = number
        do {
            if (n > 1000) {
                buf.insert(0, ",${n % 1000}")
            } else {
                buf.insert(0, n)
                break
            }
            n /= 1000
        } while (true)
        return buf.toString()
    }
}