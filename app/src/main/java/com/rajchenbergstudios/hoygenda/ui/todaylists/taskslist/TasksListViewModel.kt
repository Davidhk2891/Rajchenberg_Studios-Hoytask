package com.rajchenbergstudios.hoygenda.ui.todaylists.taskslist

import androidx.lifecycle.*
import com.rajchenbergstudios.hoygenda.data.prefs.PreferencesManager
import com.rajchenbergstudios.hoygenda.data.prefs.SortOrder
import com.rajchenbergstudios.hoygenda.data.today.task.Task
import com.rajchenbergstudios.hoygenda.data.today.task.TaskDao
import com.rajchenbergstudios.hoygenda.ui.activity.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// private const val TAG = "TasksListViewModel"

@ExperimentalCoroutinesApi
@HiltViewModel
class TasksListViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    state: SavedStateHandle
) : ViewModel(){

    val searchQuery = state.getLiveData("searchQuery", "")

    // DataStore
    val preferencesFlow = preferencesManager.preferencesFlow

    // Tasks Channel
    private val tasksEventChannel = Channel<TaskEvent>()

    // Tasks Event
    val tasksEvent = tasksEventChannel.receiveAsFlow()

    private val tasksFlow = combine(
        searchQuery.asFlow(),
        preferencesFlow
    ) { searchQuery, filterPreferences ->
        Pair(searchQuery, filterPreferences)
    }.flatMapLatest { (searchQuery, filterPreferences) ->
        taskDao.getTodays(searchQuery, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

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

    fun onAddNewTaskClick() = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToAddTaskScreen)
    }

    fun onAddTasksFromSetClick() = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToAddTasksFromSetBottomSheet)
    }

    fun onFragmentResult(result: Int, message: String?) {
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")
            CREATE_SET_RESULT_OK -> showTaskSavedInNewOrOldSetConfirmationMessage("Task added to new set")
            EDIT_SET_RESULT_OK ->   showTaskSavedInNewOrOldSetConfirmationMessage(message)
            ADD_TASK_FROM_SET_RESULT_OK -> showTaskAddedFromSetConfirmationMessage(message)
        }
    }

    private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.ShowTaskSavedConfirmationMessage(text))
    }

    private fun showTaskSavedInNewOrOldSetConfirmationMessage(text: String?) = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.ShowTaskSavedInNewOrOldSetConfirmationMessage(text))
    }

    private fun showTaskAddedFromSetConfirmationMessage(text: String?) = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.ShowTaskAddedFromSetConfirmationMessage(text))
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

    val tasks = tasksFlow.asLiveData()

    sealed class TaskEvent {
        object NavigateToDeleteAllScreen : TaskEvent()
        object NavigateToDeleteAllCompletedScreen : TaskEvent()
        object NavigateToAddTaskScreen : TaskEvent()
        object NavigateToAddTasksFromSetBottomSheet : TaskEvent()
        data class NavigateToEditTaskScreen(val task: Task) : TaskEvent()
        data class NavigateToAddTaskToSetBottomSheet(val task: Task) : TaskEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) : TaskEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String) : TaskEvent()
        data class ShowTaskSavedInNewOrOldSetConfirmationMessage(val msg: String?) : TaskEvent()
        data class ShowTaskAddedFromSetConfirmationMessage(val msg: String?) : TaskEvent()
    }
}