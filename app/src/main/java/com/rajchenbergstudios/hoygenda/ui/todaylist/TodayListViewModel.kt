package com.rajchenbergstudios.hoygenda.ui.todaylist

import androidx.lifecycle.*
import com.rajchenbergstudios.hoygenda.data.prefs.PreferencesManager
import com.rajchenbergstudios.hoygenda.data.prefs.SortOrder
import com.rajchenbergstudios.hoygenda.data.today.Today
import com.rajchenbergstudios.hoygenda.data.today.TodayDao
import com.rajchenbergstudios.hoygenda.ui.activity.*
import com.rajchenbergstudios.hoygenda.utils.HGDADateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// private const val TAG = "TodayListViewModel"

@ExperimentalCoroutinesApi
@HiltViewModel
class TodayListViewModel @Inject constructor(
    private val todayDao: TodayDao,
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
        todayDao.getTodays(searchQuery, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedSelected(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSelected(today: Today) = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToEditTaskScreen(today))
    }

    fun onTaskCheckedChanged(today: Today, isChecked: Boolean) = viewModelScope.launch {
        todayDao.update(today.copy(completed = isChecked))
    }

    fun onTaskSwiped(today: Today) = viewModelScope.launch {
        todayDao.delete(today)
        tasksEventChannel.send(TaskEvent.ShowUndoDeleteTaskMessage(today))
    }

    fun onUndoDeleteClick(today: Today) = viewModelScope.launch {
        todayDao.insert(today)
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

    fun getCurrentDayOfMonth(): String {
        return HGDADateUtils.currentDayOfMonthFormatted
    }

    fun getCurrentMonth(): String {
        return HGDADateUtils.currentMonthFormatted
    }

    fun getCurrentYear(): String {
        return HGDADateUtils.currentYearFormatted
    }

    fun getCurrentDayOfWeek(): String {
        return HGDADateUtils.currentDayOfWeekFormatted
    }

    fun onTaskLongSelected(today: Today) = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToAddTaskToSetBottomSheet(today))
    }

    val tasks = tasksFlow.asLiveData()

    sealed class TaskEvent {
        object NavigateToDeleteAllScreen : TaskEvent()
        object NavigateToDeleteAllCompletedScreen : TaskEvent()
        object NavigateToAddTaskScreen : TaskEvent()
        object NavigateToAddTasksFromSetBottomSheet : TaskEvent()
        data class NavigateToEditTaskScreen(val today: Today) : TaskEvent()
        data class NavigateToAddTaskToSetBottomSheet(val today: Today) : TaskEvent()
        data class ShowUndoDeleteTaskMessage(val today: Today) : TaskEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String) : TaskEvent()
        data class ShowTaskSavedInNewOrOldSetConfirmationMessage(val msg: String?) : TaskEvent()
        data class ShowTaskAddedFromSetConfirmationMessage(val msg: String?) : TaskEvent()
    }
}