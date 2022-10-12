package com.rajchenbergstudios.hoytask.ui.createtaskset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoytask.data.taskset.TaskSet
import com.rajchenbergstudios.hoytask.data.taskset.TaskSetDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateTaskSetDialogViewModel @Inject constructor(
    private val taskSetDao: TaskSetDao,
) : ViewModel(){

    // createTaskSet Channel
    private val createTaskSetEventChannel = Channel<CreateTaskSetEvent>()

    // createTaskSet Event
    val createTaskSetEvent = createTaskSetEventChannel.receiveAsFlow()

    fun onCreateTaskSetClick(title: String) {
        if (title.isEmpty() || title.isBlank()) {
            showInvalidInputMessage()
            return
        }

        createTaskSet(title)
    }

    private fun createTaskSet(title: String) = viewModelScope.launch {
        taskSetDao.insert(TaskSet(title = title))
        createTaskSetEventChannel.send(CreateTaskSetEvent.GoToSetAndShowSetCreatedConfirmationMessage(title, "Set created"))
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
    }
}