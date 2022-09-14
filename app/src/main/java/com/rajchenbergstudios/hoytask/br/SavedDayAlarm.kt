package com.rajchenbergstudios.hoytask.br

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object SavedDayAlarm {
    /*
    private fun setAlarm(timeInMillis: Long) {
      val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
      val intent = Intent(this, MyAlarm::class.java)
      val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
      alarmManager.setRepeating(AlarmManager.RTC, timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
      Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show()
   }
     */
    fun setDaySavingAlarm(context: Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, SavedDayAlarm::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        //alarmManager.setInexactRepeating(AlarmManager.RTC, )
    }
}