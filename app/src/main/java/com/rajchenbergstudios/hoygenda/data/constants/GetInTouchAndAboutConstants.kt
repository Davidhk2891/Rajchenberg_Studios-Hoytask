package com.rajchenbergstudios.hoygenda.data.constants

import android.os.Build
import com.rajchenbergstudios.hoygenda.BuildConfig

class GetInTouchAndAboutConstants {
    companion object {
        val versionCode = BuildConfig.VERSION_CODE
        val versionName = BuildConfig.VERSION_NAME
        val osApi = Build.VERSION.SDK_INT
        val deviceModel: String = Build.MODEL

        const val SUPPORT_EMAIL_RECIPIENT = "support@rajchenbergstudios.com"
        const val SUPPORT_EMAIL_SUBJECT = "Hoygenda - Support request"

        val SUPPORT_EMAIL_CONTENT = "\n\nWrite your message above\n" +
                "App version: $versionCode $versionName\n" +
                "Android version: $osApi\n" +
                "Device: $deviceModel"
    }
}