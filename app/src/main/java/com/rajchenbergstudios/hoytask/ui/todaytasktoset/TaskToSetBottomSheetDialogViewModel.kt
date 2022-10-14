package com.rajchenbergstudios.hoytask.ui.todaytasktoset

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoytask.data.task.Task
import com.rajchenbergstudios.hoytask.data.task.TaskDao
import com.rajchenbergstudios.hoytask.data.taskinset.TaskInSetDao
import com.rajchenbergstudios.hoytask.ui.CREATE_SET_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskToSetBottomSheetDialogViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val taskInSetDao: TaskInSetDao,
    state: SavedStateHandle
) : ViewModel(){

    val task = state.get<Task>("task")

    // TaskToSet Channel
    private val taskToSetChannel = Channel<TaskToSetEvent>()

    // TaskToSet Event
    val taskToSetEvent = taskToSetChannel.receiveAsFlow()

    fun onCreateSetClicked() = viewModelScope.launch {
        taskToSetChannel.send(TaskToSetEvent.NavigateToCreateTaskSetDialog(task))
    }

    fun onCreateSetResult(result: Int) = viewModelScope.launch {
        when (result) {
            CREATE_SET_RESULT_OK -> navigateBackWithResultFromSetCreatedWithTask(result)
        }
    }

    private fun navigateBackWithResultFromSetCreatedWithTask(result: Int) = viewModelScope.launch {
        taskToSetChannel.send(TaskToSetEvent.NavigateBackWithResultFromSetCreatedWithTask(result))
    }

    sealed class TaskToSetEvent {
        data class NavigateToCreateTaskSetDialog(val task: Task?) : TaskToSetEvent()
        data class NavigateBackWithResultFromSetCreatedWithTask(val result: Int) : TaskToSetEvent()
    }
}