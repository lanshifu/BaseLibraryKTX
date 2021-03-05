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

    fun quickSort(array: ArrayList<Int>) :ArrayList<Int>{
        if (array.size <= 1){
            return array
        }

        var middle = array[0]
        var leftArray = array.filter { it<middle }
        var rightArray = array.filter { it>middle }
        var resultArray = ArrayList<Int>(leftArray.size + 1 + rightArray.size)
        resultArray.addAll(quickSort(leftArray as ArrayList<Int>))
        resultArray.add(middle)
        resultArray.addAll(quickSort(rightArray as ArrayList<Int>))
        return resultArray
    }

    //最大连续子串
    fun sameSize(){

    }
}