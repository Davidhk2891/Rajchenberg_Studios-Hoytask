package com.rajchenbergstudios.hoytask.ui.deleteall

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rajchenbergstudios.hoytask.data.task.TaskDao
import com.rajchenbergstudios.hoytask.data.taskinset.TaskInSetDao
import com.rajchenbergstudios.hoytask.data.taskset.TaskSetDao
import com.rajchenbergstudios.hoytask.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllDialogViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val taskSetDao: TaskSetDao,
    private val taskInSetDao: TaskInSetDao,
    state: SavedStateHandle,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel(){

    private val origin = state.get<Int>("origin")

    fun onConfirmClick() {
        when (origin) {
            1 -> {deleteAllCompletedTasks()}
            2 -> {deleteAllSetsWithTasks()}
            3 -> {deleteAllTasks()}
        }
    }

    private fun deleteAllCompletedTasks() = applicationScope.launch{
        taskDao.deleteAllCompleted()
    }

    private fun deleteAllTasks() = applicationScope.launch {
        taskDao.nukeTaskTable()
    }

    private fun deleteAllSetsWithTasks() = applicationScope.launch {
        taskInSetDao.deleteAllTasksInSets()
        taskSetDao.deleteAllSets()
    }

    fun populateMessage(): String {
        var message = ""
        when (origin) {
            1 -> {message = "Do you really want to delete all completed tasks?"}
            2 -> {message = "Do you really want to delete all sets?"}
            3 -> {message = "Do you really want to delete all tasks?"}
        }
        return message
    }
}