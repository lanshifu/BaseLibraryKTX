@file:Suppress("UNCHECKED_CAST")

package com.lanshifu.lib.ext.sharedpreferences

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.lanshifu.lib.Ktx
import com.lanshifu.lib.ext.logv
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Get the Editor instance and commit/apply
 * @param commit if true , use [SharedPreferences.Editor.commit], otherwise , use [SharedPreferences.Editor.commit]
 * @param action invoke the action after get the [SharedPreferences.Editor] instance
 */
inline fun SharedPreferences.edit(
    commit: Boolean = false,
    action: SharedPreferences.Editor.() -> Unit
) {
    val editor = edit()
    action(editor)
    if (commit)
        editor.commit()
    else
        editor.apply()
}

/**
 * Return the SharedPreferences instance
 * @param name Desired preferences file. Default value is the packageName
 * @param mode Operating mode. Default value is [Context.MODE_PRIVATE]
 */
fun Context.sp(name: String = packageName, mode: Int = Context.MODE_PRIVATE): SharedPreferences =
    getSharedPreferences(name, mode)

fun Activity.sp(name: String = packageName, mode: Int = Context.MODE_PRIVATE): SharedPreferences =
    getSharedPreferences(name, mode)

/**
 * Set a [T] value in the preferences editor, to be written back once
 * commit() or apply() are called.
 *
 * @param key The name of the preference to modify
 * @param value The new value for the preference
 * @param name Desired preferences file. Default value is the packageName
 */
fun <T> Context.putSpValue(key: String, value: T, name: String = packageName) = sp(name).edit {
    "putSpValue,key=$key,value=$value".logv()
    when (value) {
        is Long -> putLong(key, value)
        is String -> putString(key, value)
        is Int -> putInt(key, value)
        is Boolean -> putBoolean(key, value)
        is Float -> putFloat(key, value)
        else -> putString(key, serialize(value))
    }
}

fun <T> Activity.putSpValue(key: String, value: T, name: String = packageName) = sp(name).edit {
    "putSpValue,key=$key,value=$value".logv()
    when (value) {
        is Long -> putLong(key, value)
        is String -> putString(key, value)
        is Int -> putInt(key, value)
        is Boolean -> putBoolean(key, value)
        is Float -> putFloat(key, value)
        else -> putString(key, serialize(value))
    }
}

/**
 * Retrieve a [T] value from the preferences
 *
 * @param key The name of the preference to retrieve.
 * @param default Value to return if this preference does not exist.
 * @param name Desired preferences file. Default value is the packageName
 */
fun <T> Context.getSpValue(key: String, default: T, name: String = packageName): T = sp(name).run {
    val result = when (default) {
        is Long -> getLong(key, default)
        is String -> getString(key, default)
        is Int -> getInt(key, default)
        is Boolean -> getBoolean(key, default)
        is Float -> getFloat(key, default)
        else -> deSerialization(getString(key, serialize(default)))
    }
    "getSpValue,key=$key,result=$result".logv()
    result as T
}

fun <T> Activity.getSpValue(key: String, default: T, name: String = packageName): T = sp(name).run {
    val result = when (default) {
        is Long -> getLong(key, default)
        is String -> getString(key, default)
        is Int -> getInt(key, default)
        is Boolean -> getBoolean(key, default)
        is Float -> getFloat(key, default)
        else -> deSerialization(getString(key, serialize(default)))
    }
    "getSpValue,key=$key,result=$result".logv()
    return result as T
}


fun <T> Any.getSpValueEasy(key: String, default: T, name: String = Ktx.app.packageName): T =
    Ktx.app.sp(name).run {
        return Ktx.app.getSpValue(key, default, name)
    }


fun <T> Any.putSpValueEasy(key: String, default: T, name: String = Ktx.app.packageName) =
    Ktx.app.sp(name).edit {
        Ktx.app.putSpValue(key, default, name)
    }

private fun <T> serialize(obj: T): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    val objectOutputStream = ObjectOutputStream(
        byteArrayOutputStream
    )
    objectOutputStream.writeObject(obj)
    var serStr = byteArrayOutputStream.toString("ISO-8859-1")
    serStr = URLEncoder.encode(serStr, "UTF-8")
    objectOutputStream.close()
    byteArrayOutputStream.close()
    return serStr
}

private fun <T> deSerialization(str: String?): T {
    val redStr = URLDecoder.decode(str, "UTF-8")
    val byteArrayInputStream = ByteArrayInputStream(
        redStr.toByteArray(charset("ISO-8859-1"))
    )
    val objectInputStream = ObjectInputStream(
        byteArrayInputStream
    )
    val obj = objectInputStream.readObject() as T
    objectInputStream.close()
    byteArrayInputStream.close()
    return obj
}