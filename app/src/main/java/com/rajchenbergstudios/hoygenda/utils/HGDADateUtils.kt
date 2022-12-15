package com.rajchenbergstudios.hoygenda.utils

import java.time.LocalDate

object HGDADateUtils {

    val today = LocalDate.now()
    val currentDayOfWeekFormatted: String
        get() = today.dayOfWeek.toString()

    val currentDayOfMonthFormatted: String
        get() = if (today.dayOfMonth < 10)
            "0${today.dayOfMonth}"
        else
            today.dayOfMonth.toString()


    val currentMonthFormatted: String
        get() = today.month.toString()

    val currentYearFormatted: String
        get() = today.year.toString()
}