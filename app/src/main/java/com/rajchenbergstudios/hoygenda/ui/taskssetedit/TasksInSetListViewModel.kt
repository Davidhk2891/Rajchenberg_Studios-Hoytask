package com.rajchenbergstudios.hoygenda.ui.taskssetedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoygenda.data.taskinset.TaskInSet
import com.rajchenbergstudios.hoygenda.data.taskinset.TaskInSetDao
import com.rajchenbergstudios.hoygenda.ui.activity.ADD_TASK_IN_SET_OK
import com.rajchenbergstudios.hoygenda.ui.activity.EDIT_TASK_IN_SET_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// private const val TAG = "TasksInSetListViewModel"

@HiltViewModel
class TasksInSetListViewModel @Inject constructor(
    private val taskInSetDao: TaskInSetDao,
    state: SavedStateHandle
) : ViewModel(){

    // Tasks in set channel
    private val tasksInSetsChannel = Channel<TaskInSetEvent>()

    // Tasks in set event
    val tasksInSetsEvent = tasksInSetsChannel.receiveAsFlow()

    private val taskSetTitle = state.get<String>("settitle")

    val tasksSet = taskSetTitle?.let {
        taskInSetDao.getTasksInSet(taskSetTitle).asLiveData()
    }

    fun onGetTaskSetTitle(): String? {
        return taskSetTitle
    }

    fun onTaskInSetSwiped(taskInSet: TaskInSet) = viewModelScope.launch {
        taskInSetDao.delete(taskInSet)
        tasksInSetsChannel.send(TaskInSetEvent.ShowUndoDeleteTaskMessage(taskInSet))
    }

    fun onUndoDeleteClick(taskInSet: TaskInSet) = viewModelScope.launch {
        taskInSetDao.insert(taskInSet)
    }

    fun onTaskInSetSelected(taskInSet: TaskInSet) = viewModelScope.launch {
        tasksInSetsChannel.send(TaskInSetEvent.NavigateToEditTaskInSetScreen(taskInSet))
    }

    fun onAddTaskInSetClick(taskInSet: TaskInSet) = viewModelScope.launch {
        tasksInSetsChannel.send(TaskInSetEvent.NavigateToAddTaskInSetScreen(taskInSet))
    }

    fun onAddEditResults(result: Int) {
        when (result) {
            ADD_TASK_IN_SET_OK -> {
                showTaskSavedConfirmationMessage("Task in Set added")
            }
            EDIT_TASK_IN_SET_OK -> {
                showTaskSavedConfirmationMessage("Task in Set updated")
            }
        }
    }

    private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch {
        tasksInSetsChannel.send(TaskInSetEvent.ShowTaskInSetSavedConfirmationMessage(text))
    }

    sealed class TaskInSetEvent {
        data class NavigateToAddTaskInSetScreen(val taskInSet: TaskInSet) : TaskInSetEvent()
        data class NavigateToEditTaskInSetScreen(val taskInSet: TaskInSet): TaskInSetEvent()
        data class ShowUndoDeleteTaskMessage(val taskInSet: TaskInSet) : TaskInSetEvent()
        data class ShowTaskInSetSavedConfirmationMessage(val msg: String) : TaskInSetEvent()
    }
}