package com.rajchenbergstudios.hoytask.ui.taskssetedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rajchenbergstudios.hoytask.data.taskset.TaskSet
import com.rajchenbergstudios.hoytask.data.taskset.TaskSetDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksSetEditListViewModel @Inject constructor(
    private val taskSetDao: TaskSetDao,
    private val state: SavedStateHandle
) : ViewModel(){

    // TODO: Check TaskAddEditViewModel
    // val task = state.get<Task>("task")

    val tasksFromset = state.get<TaskSet>("taskset")


}