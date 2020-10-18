package com.lanshifu.lib.ext

/**
 * @author lanxiaobin
 * @date 2020/9/19
 */

fun <T> Collection<T>.print(map: (T) -> String) =
    StringBuilder("\n[").also { sb ->
        //'遍历集合元素，通过 map 表达式将元素转换成感兴趣的字串，并独占一行'
        this.forEach { e -> sb.append("\n\t${map(e)},") }
        sb.append("\n]")
    }.toString()


fun <K, V> Map<K, V?>.print(map: (V?) -> String): String =
    StringBuilder("\n{").also { sb ->
        this.iterator().forEach { entry ->
            sb.append("\n\t[${entry.key}] = ${map(entry.value)}")
        }
        sb.append("\n}")
    }.toString()