package com.rajchenbergstudios.hoygenda.ui.tasksset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoygenda.data.taskinset.TaskInSet
import com.rajchenbergstudios.hoygenda.data.taskinset.TaskInSetDao
import com.rajchenbergstudios.hoygenda.data.taskset.TaskSet
import com.rajchenbergstudios.hoygenda.data.taskset.TaskSetDao
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
    private val taskInSetDao: TaskInSetDao
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

    fun onSetSwiped(set: TaskSet) = viewModelScope.launch{
        val tasksInSetList = taskInSetDao.getTasksFromSet(set.title)
        for (taskInSet in tasksInSetList) {
            taskInSetDao.delete(taskInSet)
        }
        taskSetDao.delete(set)
        taskSetsEventChannel.send(TaskSetEvent.ShowUndoDeleteSetMessage(set, tasksInSetList))
    }

    fun onUndoDeleteClick(set: TaskSet, tasksInSetList: List<TaskInSet>) = viewModelScope.launch {
        taskSetDao.insert(set)
        for (taskInSet in tasksInSetList) {
            taskInSetDao.insert(TaskInSet(taskInSet.taskInSet, taskInSet.taskInSetBigTitle))
        }
    }

    val taskSets = setsFlow.asLiveData()

    sealed class TaskSetEvent {
        object NavigateToDeleteAllSetsScreen : TaskSetEvent()
        data class NavigateToEditTaskSet(val taskSet: TaskSet) : TaskSetEvent()
        data class ShowUndoDeleteSetMessage(val taskSet: TaskSet, val tasksInSetList: List<TaskInSet>) : TaskSetEvent()
    }
}