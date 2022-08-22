package com.rajchenbergstudios.hoytask.ui.taskaddedit

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoytask.data.task.Task
import com.rajchenbergstudios.hoytask.data.task.TaskDao
import com.rajchenbergstudios.hoytask.ui.ADD_TASK_RESULT_OK
import com.rajchenbergstudios.hoytask.ui.EDIT_TASK_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "TaskEditViewModel.kt"

@HiltViewModel
class TaskAddEditViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val state: SavedStateHandle
) : ViewModel() {

    val task = state.get<Task>("task")

    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            state["taskName"] = value
        }

    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.important ?: false
        set(value) {
            field = value
            state["taskName"] = value
        }

    // AddEdit event channel
    private val addEditEventChannel = Channel<AddEditEvent>()

    // AddEdit event
    val addEditEvent = addEditEventChannel.receiveAsFlow()

    fun onSaveClick(){

        if (taskName.isBlank()) {
            Log.i(TAG, "taskName: $taskName")
            showInvalidInputMessage()
            return
        }

        if (task != null) {
            val updatedTask = task.copy(name = taskName, important = taskImportance)
            updateTask(updatedTask)
        } else {
            val newTask = Task(name = taskName, important = taskImportance)
            createTask(newTask)
        }
    }

    private fun createTask(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
        addEditEventChannel.send(AddEditEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updateTask(task: Task) = viewModelScope.launch {
        taskDao.update(task)
        addEditEventChannel.send(AddEditEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    private fun showInvalidInputMessage() = viewModelScope.launch {
        addEditEventChannel.send(AddEditEvent.ShowInvalidInputMessage("Name cannot be empty"))
    }

    sealed class AddEditEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditEvent()
    }
}