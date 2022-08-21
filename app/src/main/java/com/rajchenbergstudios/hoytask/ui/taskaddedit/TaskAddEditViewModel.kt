package com.rajchenbergstudios.hoytask.ui.taskaddedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rajchenbergstudios.hoytask.data.task.Task
import com.rajchenbergstudios.hoytask.data.task.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaskAddEditViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val state: SavedStateHandle
) : ViewModel() {

    val task = state.get<Task>("task")

    var taskName = state.get<String>("taskName") ?: task?.name ?: " "
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("taskName", value)
        }
}