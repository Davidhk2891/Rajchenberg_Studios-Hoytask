package com.rajchenbergstudios.hoytask.ui.tasksset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoytask.data.taskset.TaskSet
import com.rajchenbergstudios.hoytask.data.taskset.TaskSetDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class TasksSetsListViewModel @Inject constructor(
    private val taskSetDao: TaskSetDao,
) : ViewModel(){

    val searchQuery = MutableStateFlow("")

    // TaskSet Channel
    private val taskSetsEventChannel = Channel<TaskSetEvent>()

    // TaskSet Event
    val taskSetEvent = taskSetsEventChannel.receiveAsFlow()

    private val setsFlow = searchQuery.flatMapLatest { setsList ->
        taskSetDao.getSets(setsList)
    }

    fun onDeleteAllSetsClick() = viewModelScope.launch {
        taskSetsEventChannel.send(TaskSetEvent.NavigateToDeleteAllSetsScreen)
    }

    fun onTaskSetSelected(taskSet: TaskSet) = viewModelScope.launch {
        taskSetsEventChannel.send(TaskSetEvent.NavigateToEditTaskSet(taskSet))
    }

    val taskSets = setsFlow.asLiveData()

    sealed class TaskSetEvent {
        object NavigateToDeleteAllSetsScreen : TaskSetEvent()
        data class NavigateToEditTaskSet(val taskSet: TaskSet) : TaskSetEvent()
    }
}