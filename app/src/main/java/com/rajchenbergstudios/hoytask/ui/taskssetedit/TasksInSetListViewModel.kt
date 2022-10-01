package com.rajchenbergstudios.hoytask.ui.taskssetedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rajchenbergstudios.hoytask.data.taskinset.TaskInSetDao
import com.rajchenbergstudios.hoytask.data.taskset.TaskSet
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksInSetListViewModel @Inject constructor(
    private val taskInSetDao: TaskInSetDao,
    state: SavedStateHandle
) : ViewModel(){

    private val taskSet = state.get<TaskSet>("taskset")

    private val taskSetTitle = state.get<String>("taskSetTitle") ?: taskSet?.title

    val tasksSet = taskSetTitle?.let {
        taskInSetDao.getTasksInSet(taskSetTitle).asLiveData()
    }
}