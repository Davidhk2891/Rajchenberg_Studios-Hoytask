package com.rajchenbergstudios.hoytask.ui.createtaskset

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoytask.data.task.Task
import com.rajchenbergstudios.hoytask.data.taskinset.TaskInSet
import com.rajchenbergstudios.hoytask.data.taskinset.TaskInSetDao
import com.rajchenbergstudios.hoytask.data.taskset.TaskSet
import com.rajchenbergstudios.hoytask.data.taskset.TaskSetDao
import com.rajchenbergstudios.hoytask.ui.activity.CREATE_SET_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateTaskSetDialogViewModel @Inject constructor(
    private val taskSetDao: TaskSetDao,
    private val taskInSetDao: TaskInSetDao,
    state: SavedStateHandle
) : ViewModel(){

    val task = state.get<Task>("task")

    val origin = state.get<Int>("origin")

    private val taskName = state.get<String>("taskName") ?: task?.name

    // createTaskSet Channel
    private val createTaskSetEventChannel = Channel<CreateTaskSetEvent>()

    // createTaskSet Event
    val createTaskSetEvent = createTaskSetEventChannel.receiveAsFlow()

    fun onCreateTaskSetClick(title: String) {
        if (title.isEmpty() || title.isBlank()) {
            showInvalidInputMessage()
            return
        }
        when (origin) {
            1 -> createTaskSet(title)
            2 -> createTaskSetAddTaskNoNavigation(title)
        }
    }

    private fun createTaskSet(title: String) = viewModelScope.launch {
        taskSetDao.insert(TaskSet(title = title))
        createTaskSetEventChannel.send(CreateTaskSetEvent.GoToSetAndShowSetCreatedConfirmationMessage(title, "Set created"))
    }

    private fun createTaskSetAddTaskNoNavigation(title: String) = viewModelScope.launch {
        taskSetDao.insert(TaskSet(title = title))
        if (taskName != null) {
            taskInSetDao.insert(TaskInSet(taskInSet = taskName, taskInSetBigTitle = title))
        }
        createTaskSetEventChannel.send(CreateTaskSetEvent
            .DoNotGoToSetAndShowSetCreatedConfirmationMessage("Set created with this task", CREATE_SET_RESULT_OK))
    }

    fun onCancelTaskSetClick() = viewModelScope.launch{
        createTaskSetEventChannel.send(CreateTaskSetEvent.DoTaskSetCancellationAction)
    }

    private fun showInvalidInputMessage() = viewModelScope.launch {
        createTaskSetEventChannel.send(CreateTaskSetEvent.ShowInvalidInputMessage("Title cannot be empty"))
    }

    sealed class CreateTaskSetEvent {
        object DoTaskSetCancellationAction : CreateTaskSetEvent()
        data class ShowInvalidInputMessage(val msg: String) : CreateTaskSetEvent()
        data class GoToSetAndShowSetCreatedConfirmationMessage(val title: String, val msg: String) : CreateTaskSetEvent()
        data class DoNotGoToSetAndShowSetCreatedConfirmationMessage(val msg: String, val result: Int) : CreateTaskSetEvent()
    }
}