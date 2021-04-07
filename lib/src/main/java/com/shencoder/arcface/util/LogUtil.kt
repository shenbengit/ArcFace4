package com.shencoder.arcface.util

import android.util.Log

/**
 *
 * @author  ShenBen
 * @date    2021/02/24 17:25
 * @email   714081644@qq.com
 */
object LogUtil {
    private const val TAG = "ArcFace"

    private var logPriority = Log.VERBOSE

    @JvmStatic
    fun setLogPriority(priority: Int) {
        logPriority = priority
    }

    @JvmStatic
    fun v(msg: String) {
        if (isLoggable(Log.VERBOSE).not()) {
            return
        }
        Log.v(TAG, msg)
    }

    @JvmStatic
    fun d(msg: String) {
        if (isLoggable(Log.DEBUG).not()) {
            return
        }
        Log.d(TAG, msg)
    }

    @JvmStatic
    fun i(msg: String) {
        if (isLoggable(Log.INFO).not()) {
            return
        }
        Log.i(TAG, msg)
    }

    @JvmStatic
    fun w(msg: String) {
        if (isLoggable(Log.WARN).not()) {
            return
        }
        Log.w(TAG, msg)
    }

    @JvmStatic
    fun e(msg: String) {
        if (isLoggable(Log.ERROR).not()) {
            return
        }
        Log.e(TAG, msg)
    }

    private fun isLoggable(priority: Int): Boolean {
        return priority >= logPriority
    }
}