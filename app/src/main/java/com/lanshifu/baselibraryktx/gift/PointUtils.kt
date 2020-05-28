package com.lanshifu.baselibraryktx.gift

import android.content.Context
import android.graphics.Point
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * @author Jenly [Jenly](mailto:jenly1314@gmail.com)
 * @since 2017/3/28
 */
class PointUtils private constructor() {
    companion object {
        private const val POINTS = "points"
        private const val POINT = "point"
        private const val X = "x"
        private const val Y = "y"

        @Throws(IOException::class)
        fun getListPointByResourceJson(
            context: Context,
            fileName: String?
        ): List<Point>? {
            val inputStream =
                context.classLoader.getResourceAsStream(fileName)
            return getListPointByJson(
                inputStreamToString(
                    inputStream
                )
            )
        }

        @Throws(IOException::class)
        fun inputStreamToString(inputStream: InputStream?): String? {
            if (inputStream != null) {
                val sb = StringBuilder()
                val data = ByteArray(4096)
                var len: Int
                while (inputStream.read(data).also { len = it } != -1) {
                    sb.append(String(data, 0, len))
                }
                inputStream?.close()
                return sb.toString()
            }
            return null
        }

        @Throws(IOException::class)
        fun getListPointByJsonInputStream(inputStream: InputStream?): List<Point>? {
            return getListPointByJson(
                inputStreamToString(
                    inputStream
                )
            )
        }

        fun getListPointByJson(json: String?): List<Point>? {
            if (json != null) {
                try {
                    val list: MutableList<Point> =
                        ArrayList()
                    val jsonObject = JSONObject(json)
                    val jsonArray = jsonObject.getJSONObject(POINTS)
                        .getJSONArray(POINT)
                    val size = jsonArray.length()
                    for (i in 0 until size) {
                        val jsonPoint = jsonArray.getJSONObject(i)
                        val point = Point(
                            jsonPoint.getInt(X),
                            jsonPoint.getInt(Y)
                        )
                        list.add(point)
                    }
                    return list
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            return null
        }
    }

    init {
        throw AssertionError()
    }
}