package com.lanshifu.lib.base.mmkv

import android.content.SharedPreferences
import com.lanshifu.lib.ext.logd
import com.tencent.mmkv.MMKV

/**
 * @author lanxiaobin
 * @date 2020-04-21
 *
 * MMKV 代理，统一打印日志，方便定位问题
 *
 * 支持多进程
 */
class MMKVDelegate {

    companion object{


        private var mmkv: MMKV = MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null)

        fun defaultMMKV(): MMKVDelegate {
            return MMKVDelegate()
        }
    }


    fun getString(key: String, defValue: String?): String? {
        var value = mmkv.getString(key, defValue)
        logd("getString,key=$key,value=$value")
        return value
    }

    fun putString(key: String, value: String?): SharedPreferences.Editor {
        logd("putString,key=$key,value=$value")
        mmkv.putString(key, value)
        return mmkv
    }

    fun getInt(key: String, defValue: Int): Int {
        var value = mmkv.getInt(key, defValue)
        logd("getInt,key=$key,value=$value")
        return value
    }

    fun putInt(key: String, value: Int): SharedPreferences.Editor {
        logd("putInt,key=$key,value=$value")
        mmkv.putInt(key, value)
        return mmkv
    }

    fun getLong(key: String, defValue: Long): Long {
        var value = mmkv.getLong(key, defValue)
        logd("getLong,key=$key,value=$value")
        return value
    }

    fun putLong(key: String, value: Long): SharedPreferences.Editor {
        logd("putLong,key=$key,value=$value")
        mmkv.putLong(key, value)
        return mmkv
    }

    fun getFloat(key: String, defValue: Float): Float {
        var value = mmkv.decodeFloat(key, defValue)
        logd("getFloat,key=$key,value=$value")
        return value
    }

    fun putFloat(key: String, value: Float): SharedPreferences.Editor {
        logd("putFloat,key=$key,value=$value")
        mmkv.getFloat(key, value)
        return mmkv
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        var value = mmkv.getBoolean(key, defValue)
        logd("getBoolean,key=$key,value=$value")
        return value
    }

    fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
        logd("putBoolean,key=$key,value=$value")
        mmkv.putBoolean(key, value)
        return mmkv
    }

    fun getBytes(key: String, defValue: ByteArray?): ByteArray? {
        var value = mmkv.getBytes(key, defValue)
        logd("getBytes,key=$key,value=$value")
        return value
    }

    fun putBytes(key: String, value: ByteArray): SharedPreferences.Editor {
        logd("putBytes,key=$key,value=$value")
        mmkv.putBytes(key,value)
        return mmkv
    }

    fun remove(key: String): SharedPreferences.Editor {
        logd("remove,key=$key")
        mmkv.remove(key)
        return mmkv
    }

}