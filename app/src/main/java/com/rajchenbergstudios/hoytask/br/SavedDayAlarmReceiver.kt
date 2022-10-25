package com.rajchenbergstudios.hoytask.br

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rajchenbergstudios.hoytask.data.day.Day
import com.rajchenbergstudios.hoytask.data.day.DayDao
import com.rajchenbergstudios.hoytask.data.task.TaskDao
import com.rajchenbergstudios.hoytask.utils.CurrentDate
import kotlinx.coroutines.*
import javax.inject.Inject

class SavedDayAlarmReceiver @Inject constructor(
    val taskDao: TaskDao?,
    val dayDao: DayDao?
) : BroadcastReceiver() {

    constructor() : this(null, null)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            if (taskDao != null) {
                if (taskDao.getTasks().isNotEmpty()) {
                    val list = taskDao.getTasks()
                    dayDao?.insert(
                        Day
                            (
                            CurrentDate.currentDayOfWeekFormatted,
                            CurrentDate.currentDayOfMonthFormatted,
                            CurrentDate.currentMonthFormatted,
                            CurrentDate.currentYearFormatted,
                            list
                        )
                    )
                    taskDao.nukeTaskTable()
                }
            }
        }
        scope.cancel()
    }
}