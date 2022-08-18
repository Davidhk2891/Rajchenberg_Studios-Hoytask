package com.rajchenbergstudios.hoytask.ui.taskslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rajchenbergstudios.hoytask.data.task.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class TasksListViewModel @Inject constructor(
    private val taskDao: TaskDao
) : ViewModel(){

    val searchQuery = MutableStateFlow("")

    private val tasksFlow = searchQuery.flatMapLatest { tasksList ->
        taskDao.getTasks(tasksList)
    }

    val tasks = tasksFlow.asLiveData()
}