package com.example.lab2.core

import android.util.Log

object AppLogger {
    private const val TAG = "Lab2"

    fun d(msg: String) = Log.d(TAG, msg)
    fun i(msg: String) = Log.i(TAG, msg)
    fun w(msg: String, tr: Throwable? = null) = Log.w(TAG, msg, tr)
    fun e(msg: String, tr: Throwable? = null) = Log.e(TAG, msg, tr)
}