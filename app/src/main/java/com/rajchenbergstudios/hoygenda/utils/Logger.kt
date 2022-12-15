package com.rajchenbergstudios.hoygenda.utils

import android.util.Log

object Logger {

    const val APPLICATION_LOG_TAG = "HOYTASK"

//    fun i(classTag: String, methodTag: String, message: String?) {
//        val logMessage = "[$classTag::$methodTag] -> $message"
//        if (UtilApp.isDebuggable) {
//            Log.i(APPLICATION_LOG_TAG, logMessage)
//
//        } else {
//            if (com.fiverr.util.BuildConfig.FORCE_LOGS) {
//                Log.i(APPLICATION_LOG_TAG, logMessage)
//            }
//
//            FirebaseCrashlytics.getInstance().log(logMessage)
//        }
//    }

    fun i(classTag: String = "DEFAULT", methodTag: String, message: String?) {
        val logMessage = "[$classTag::$methodTag] -> $message"
        Log.i(APPLICATION_LOG_TAG, logMessage)
    }
}