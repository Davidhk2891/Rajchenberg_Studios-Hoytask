package com.rajchenbergstudios.hoytask.ui.taskssetedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoytask.data.taskinset.TaskInSet
import com.rajchenbergstudios.hoytask.data.taskinset.TaskInSetDao
import com.rajchenbergstudios.hoytask.data.taskset.TaskSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksInSetListViewModel @Inject constructor(
    private val taskInSetDao: TaskInSetDao,
    state: SavedStateHandle
) : ViewModel(){

    // Tasks in set channel
    private val tasksInSetsChannel = Channel<TaskInSetEvent>()

    // Tasks in set event
    val tasksInSetsEvent = tasksInSetsChannel.receiveAsFlow()

    private val taskSet = state.get<TaskSet>("taskset")

    private val taskSetTitle = state.get<String>("taskSetTitle") ?: taskSet?.title

    val tasksSet = taskSetTitle?.let {
        taskInSetDao.getTasksInSet(taskSetTitle).asLiveData()
    }

    fun onTaskInSetSwiped(taskInSet: TaskInSet) = viewModelScope.launch {
        taskInSetDao.delete(taskInSet)
        tasksInSetsChannel.send(TaskInSetEvent.ShowUndoDeleteTaskMessage(taskInSet))
    }

    fun onUndoDeleteClick(taskInSet: TaskInSet) = viewModelScope.launch {
        taskInSetDao.insert(taskInSet)
    }

    sealed class TaskInSetEvent {
        data class ShowUndoDeleteTaskMessage(val taskInSet: TaskInSet) : TaskInSetEvent()
    }
}