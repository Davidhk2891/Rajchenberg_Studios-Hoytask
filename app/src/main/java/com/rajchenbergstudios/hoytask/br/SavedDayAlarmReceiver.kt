package com.rajchenbergstudios.hoytask.br

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rajchenbergstudios.hoytask.data.day.Day
import com.rajchenbergstudios.hoytask.data.day.DayDao
import com.rajchenbergstudios.hoytask.data.task.TaskDao
import com.rajchenbergstudios.hoytask.util.Converters
import com.rajchenbergstudios.hoytask.util.CurrentDate
import kotlinx.coroutines.*
import javax.inject.Inject

class SavedDayAlarmReceiver @Inject constructor(
    val taskDao: TaskDao,
    val dayDao: DayDao
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            if (taskDao.getTasks().isNotEmpty()) {
                dayDao.insert(Day
                    (CurrentDate.currentDayOfWeekFormatted,
                     CurrentDate.currentDayOfMonthFormatted,
                     CurrentDate.currentMonthFormatted,
                     CurrentDate.currentYearFormatted,
                     Converters.fromTaskListToJson(taskDao.getTasks())))
                taskDao.nukeTaskTable()
            }
        }
        scope.cancel()
    }
}