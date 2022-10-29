package com.rajchenbergstudios.hoytask.features

import com.rajchenbergstudios.hoytask.data.task.Task
import com.rajchenbergstudios.hoytask.data.task.TaskDao
import com.rajchenbergstudios.hoytask.utils.Logger
import javax.inject.Inject

const val TAG = "DayManager.kt"

class DayManager @Inject constructor(
    private val taskDao: TaskDao
){

    /*
    I need to:
    1) Check if there is a task saved in Today. For that I need access to TaskDao
    2) If there are tasks, check the date of the last added task (without time, just the date)

    Tips:
    - Let's do this step by step. Complete one step, run and test -> repeat
    - do it in TasksListViewModel for now. Later down your journey you'll learn how to do it in MainActivity and then in Application class.
    - DayManager operations, along with list size status, will take place while a splash screen is displayed
    */

    fun onTodayTasksSearchCheck() {
//        val tasksList: List<Task> = taskDao.getTasks()
//        if (tasksList.isEmpty()) return
//        Logger.i(TAG, "onTodayTasksSearch", "there are some tasks: $tasksList")
    }
}