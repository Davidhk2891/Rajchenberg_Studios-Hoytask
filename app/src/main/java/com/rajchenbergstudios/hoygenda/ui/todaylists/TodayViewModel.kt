package com.rajchenbergstudios.hoygenda.ui.todaylists

import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoygenda.ui.activity.*
import com.rajchenbergstudios.hoygenda.ui.todaylists.taskslist.TasksListViewModel
import com.rajchenbergstudios.hoygenda.utils.HGDADateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class TodayViewModel @Inject constructor(

) : ViewModel() {

    // Today Channel
    private val todayEventChannel = Channel<TodayEvent>()

    val todayEvent = todayEventChannel.receiveAsFlow()

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

    fun onAddNewTaskClick() = viewModelScope.launch {
        todayEventChannel.send(TodayEvent.NavigateToAddTaskScreen)
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

    fun onAddTasksFromSetClick() = viewModelScope.launch {
        todayEventChannel.send(TodayEvent.NavigateToAddTasksFromSetBottomSheet)
    }

    private fun showTaskAddedFromSetConfirmationMessage(message: String?) = viewModelScope.launch {
        todayEventChannel.send(TodayEvent.ShowTaskAddedFromSetConfirmationMessage(message))
    }

    private fun showTaskSavedInNewOrOldSetConfirmationMessage(message: String?) = viewModelScope.launch {
        todayEventChannel.send(TodayEvent.ShowTaskSavedInNewOrOldSetConfirmationMessage(message))
    }

    private fun showTaskSavedConfirmationMessage(msg: String) = viewModelScope.launch {
        todayEventChannel.send(TodayEvent.ShowTaskSavedConfirmationMessage(msg))
    }

    sealed class TodayEvent {
        object NavigateToAddTaskScreen : TodayEvent()
        object NavigateToAddTasksFromSetBottomSheet : TodayEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String) : TodayEvent()
        data class ShowTaskSavedInNewOrOldSetConfirmationMessage(val msg: String?) : TodayEvent()
        data class ShowTaskAddedFromSetConfirmationMessage(val msg: String?) : TodayEvent()
    }
}