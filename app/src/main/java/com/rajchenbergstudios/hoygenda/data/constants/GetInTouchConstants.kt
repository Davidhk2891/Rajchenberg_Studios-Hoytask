package com.rajchenbergstudios.hoygenda.data.constants

import android.os.Build
import com.rajchenbergstudios.hoygenda.BuildConfig

class GetInTouchConstants {
    companion object {
        const val SUPPORT_EMAIL_RECIPIENT = "support@rajchenbergstudios.com"
        const val SUPPORT_EMAIL_SUBJECT = "Hoygenda - Support request"
        val SUPPORT_EMAIL_CONTENT = "\n\nWrite your message above\n" +
                "App version: ${BuildConfig.VERSION_CODE} ${BuildConfig.VERSION_NAME}\n" +
                "Android version: ${Build.VERSION.SDK_INT}\n" +
                "Device: ${Build.MODEL}"
    }
}