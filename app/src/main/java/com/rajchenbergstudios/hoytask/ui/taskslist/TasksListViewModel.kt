package com.rajchenbergstudios.hoytask.ui.taskslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rajchenbergstudios.hoytask.data.task.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksListViewModel @Inject constructor(
    private val taskDao: TaskDao
) : ViewModel(){

    val tasks = taskDao.getTasks().asLiveData()
}