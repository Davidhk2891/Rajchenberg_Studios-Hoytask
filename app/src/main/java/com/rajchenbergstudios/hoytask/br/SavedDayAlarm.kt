package com.rajchenbergstudios.hoytask.br

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.rajchenbergstudios.hoytask.util.Logger
import java.util.*

private const val TAG = "SavedDayAlarm"

object SavedDayAlarm {

    fun setDaySavingAlarm(context: Context){
        val daySavingHour: Calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 24)
            set(Calendar.MINUTE, 1)
        }

        val intent = Intent(context, SavedDayAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC, daySavingHour.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
        Logger.i(TAG, "setDaySavingAlarm", "alarm set")
    }
}