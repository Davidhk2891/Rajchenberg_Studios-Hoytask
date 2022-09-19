package com.rajchenbergstudios.hoytask.br

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rajchenbergstudios.hoytask.data.task.TaskDao
import kotlinx.coroutines.*

class SavedDayAlarmReceiver : BroadcastReceiver() {

    val taskDao: TaskDao = TaskDao

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return
        // Save current day object to DB if there are entries
        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            if (taskDao.getTasks().isNotEmpty()) {
                
            }
        }
        // Should be able to access db since its a singleton
        // The only available tasks are the ones currently in  the current day
        // You want to get the items from the database
        // It is just an IO operation to room. So use setRepeating

        /*
                val task1 = Task("Work", important = true, completed = true)
                val task2 = Task("Play", important = true, completed = true)
                val task3 = Task("sleep", important = true)

                val tasks = listOf(task1, task2, task3)
                dayDao.insert(Day("Friday", "26", "August", "2022", 1, Converters.fromTaskListToJson(tasks)))
         */
        scope.cancel()
    }
}