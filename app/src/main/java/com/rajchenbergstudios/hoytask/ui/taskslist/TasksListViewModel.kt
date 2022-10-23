package com.rajchenbergstudios.hoytask.ui.taskslist

import android.content.Context
import androidx.lifecycle.*
import com.rajchenbergstudios.hoytask.br.SavedDayAlarm
import com.rajchenbergstudios.hoytask.data.prefs.PreferencesManager
import com.rajchenbergstudios.hoytask.data.prefs.SortOrder
import com.rajchenbergstudios.hoytask.data.task.Task
import com.rajchenbergstudios.hoytask.data.task.TaskDao
import com.rajchenbergstudios.hoytask.ui.ADD_TASK_RESULT_OK
import com.rajchenbergstudios.hoytask.ui.CREATE_SET_RESULT_OK
import com.rajchenbergstudios.hoytask.ui.EDIT_SET_RESULT_OK
import com.rajchenbergstudios.hoytask.ui.EDIT_TASK_RESULT_OK
import com.rajchenbergstudios.hoytask.utils.CurrentDate
import com.rajchenbergstudios.hoytask.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "TasksListViewModel"

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
        taskDao.getTasks(searchQuery, filterPreferences.sortOrder, filterPreferences.hideCompleted)
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

    fun onFragmentResult(result: Int, message: String?) {
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")
            CREATE_SET_RESULT_OK -> showTaskSavedInNewOrOldSetConfirmationMessage("Task added to new set")
            EDIT_SET_RESULT_OK ->   showTaskSavedInNewOrOldSetConfirmationMessage(message)
        }
    }

    private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.ShowTaskSavedConfirmationMessage(text))
    }

    private fun showTaskSavedInNewOrOldSetConfirmationMessage(text: String?) = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.ShowTaskSavedInNewOrOldSetConfirmationMessage(text))
    }

    fun onDeleteAllCompletedClick() = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToDeleteAllCompletedScreen)
    }

    fun getCurrentDayOfMonth(): String {
        return CurrentDate.currentDayOfMonthFormatted
    }

    fun getCurrentMonth(): String {
        return CurrentDate.currentMonthFormatted
    }

    fun getCurrentYear(): String {
        return CurrentDate.currentYearFormatted
    }

    fun getCurrentDayOfWeek(): String {
        return CurrentDate.currentDayOfWeekFormatted
    }

    fun onSetDaySaving(context: Context) = viewModelScope.launch {
        if (preferencesManager.getDaySavingSetting() == null){
            Logger.i(TAG, "onSetDaySaving", "alarm to be set")
            SavedDayAlarm.setDaySavingAlarm(context)
            preferencesManager.setDaySavingSetting()
        } else {
            Logger.i(TAG, "onSetDaySaving", "alarm already set")
        }
    }

    fun onTaskLongSelected(task: Task) = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToAddTaskToSetBottomSheet(task))
    }

    val tasks = tasksFlow.asLiveData()

    sealed class TaskEvent {
        object NavigateToDeleteAllCompletedScreen : TaskEvent()
        object NavigateToAddTaskScreen : TaskEvent()
        data class NavigateToEditTaskScreen(val task: Task) : TaskEvent()
        data class NavigateToAddTaskToSetBottomSheet(val task: Task) : TaskEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) : TaskEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String) : TaskEvent()
        data class ShowTaskSavedInNewOrOldSetConfirmationMessage(val msg: String?) : TaskEvent()
    }
}