package com.rajchenbergstudios.hoygenda.data.constants

import com.rajchenbergstudios.hoygenda.BuildConfig

class ChangelogConstants {
    companion object {
        const val LATEST_VERSION_NAME = BuildConfig.VERSION_NAME
        const val LATEST_VERSION_CHANGES = "\u2022 Added support for Android 12\n" +
                                           "\u2022 Bug fixes\n" +
                                           "\u2022 Crash fixes"
    }
}