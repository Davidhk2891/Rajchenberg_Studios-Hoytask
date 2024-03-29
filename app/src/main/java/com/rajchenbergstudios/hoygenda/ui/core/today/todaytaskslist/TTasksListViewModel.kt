package com.rajchenbergstudios.hoygenda.ui.core.today.todaytaskslist

import androidx.lifecycle.*
import com.rajchenbergstudios.hoygenda.data.prefs.PreferencesManager
import com.rajchenbergstudios.hoygenda.data.prefs.SortOrder
import com.rajchenbergstudios.hoygenda.data.today.task.Task
import com.rajchenbergstudios.hoygenda.data.today.task.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// private const val TAG = "TTasksListViewModel"

@ExperimentalCoroutinesApi
@HiltViewModel
class TTasksListViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    state: SavedStateHandle
) : ViewModel(){

    val todaySearchQuery = state.getLiveData("searchQuery", "")

    // DataStore
    val todayPreferencesFlow = preferencesManager.todayPreferencesFlow

    // Tasks Channel
    private val tasksEventChannel = Channel<TaskEvent>()

    // Tasks Event
    val tasksEvent = tasksEventChannel.receiveAsFlow()

    private val tasksFlow = combine(
        todaySearchQuery.asFlow(),
        todayPreferencesFlow
    ) { searchQuery, filterTodayPreferences ->
        Pair(searchQuery, filterTodayPreferences)
    }.flatMapLatest { (searchQuery, filterTodayPreferences) ->
        taskDao.getTasks(searchQuery, filterTodayPreferences.sortOrder, filterTodayPreferences.hideCompleted)
    }

    val tasks = tasksFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedSelected(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSelected(task: Task) = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToEditTaskScreen(task))
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked))
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        tasksEventChannel.send(TaskEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onDeleteAllCompletedClick() = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToDeleteAllCompletedScreen)
    }

    fun onDeleteAllClick() = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToDeleteAllScreen)
    }

    fun onTaskLongSelected(task: Task) = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToAddTaskToSetBottomSheet(task))
    }

    sealed class TaskEvent {
        object NavigateToDeleteAllScreen : TaskEvent()
        object NavigateToDeleteAllCompletedScreen : TaskEvent()
        data class NavigateToEditTaskScreen(val task: Task) : TaskEvent()
        data class NavigateToAddTaskToSetBottomSheet(val task: Task) : TaskEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) : TaskEvent()
    }
}