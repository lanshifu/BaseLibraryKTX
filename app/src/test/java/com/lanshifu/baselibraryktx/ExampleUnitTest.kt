package com.lanshifu.baselibraryktx

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

        var textForNumber = TestClass.textForNumber(19010)

        println(textForNumber)

        var list = ArrayList<Int>()
        list.add(3)
        list.add(1)
        list.add(4)
        list.add(8)
        list.add(0)
        list.add(9)
        list = TestClass.quickSort(list)
        print("快速排序结果：$list")
    }
}
