package com.rajchenbergstudios.hoytask.ui.tasksset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rajchenbergstudios.hoytask.data.taskset.TaskSetDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksSetsListViewModel @Inject constructor(
    private val taskSetDao: TaskSetDao
) : ViewModel(){

    val taskSets = taskSetDao.getSets().asLiveData()
}