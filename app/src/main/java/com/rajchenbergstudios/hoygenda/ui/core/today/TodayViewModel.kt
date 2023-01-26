package com.rajchenbergstudios.hoygenda.ui.core.today


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoygenda.data.prefs.PreferencesManager
import com.rajchenbergstudios.hoygenda.di.MainThreadScope
import com.rajchenbergstudios.hoygenda.ui.activity.*
import com.rajchenbergstudios.hoygenda.utils.HGDADateUtils
import com.rajchenbergstudios.hoygenda.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "TodayViewModel"

@ExperimentalCoroutinesApi
@HiltViewModel
class TodayViewModel @Inject constructor(
    @MainThreadScope private val mainThreadScope: CoroutineScope,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // Today Channel
    private val todayEventChannel = Channel<TodayEvent>()

    // Today Event
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

    fun onAddNewJEntryClick() = viewModelScope.launch {
        todayEventChannel.send(TodayEvent.NavigateToAddJEntryScreen)
    }

    fun onFragmentResult(result: Int, message: String?) {
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")
            ADD_JENTRY_RESULT_OK -> showJEntrySavedConfirmationMessage("Journal entry added")
            EDIT_JENTRY_RESULT_OK -> showJEntrySavedConfirmationMessage("Journal entry updated")
            CREATE_SET_RESULT_OK -> showTaskSavedInNewOrOldSetConfirmationMessage("Task added to new set")
            EDIT_SET_RESULT_OK -> showTaskSavedInNewOrOldSetConfirmationMessage(message)
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

    private fun showJEntrySavedConfirmationMessage(msg: String) = viewModelScope.launch {
        todayEventChannel.send(TodayEvent.ShowJEntrySavedConfirmationMessage(msg))
    }

    fun onTutorialRedirectionEngaged() = viewModelScope.launch{
        Logger.i(TAG, "onTutorialRedirectionEngaged", "Tutorial AutoRun setting value is: ${preferencesManager.isTutorialAutoRun()}")
        if (preferencesManager.isTutorialAutoRun() == null)
            todayEventChannel.send(TodayEvent.NavigateToTutorialFragment)
    }

    fun postActionWithDelay(delay: Long, postActionCallback: PostActionListener) = mainThreadScope.launch {
        delay(delay)
        postActionCallback.onDelayFinished()
    }

    interface PostActionListener{
        fun onDelayFinished()
    }

    // Events wrapper class
    sealed class TodayEvent {
        object NavigateToAddTaskScreen : TodayEvent()
        object NavigateToAddJEntryScreen : TodayEvent()
        object NavigateToAddTasksFromSetBottomSheet : TodayEvent()
        object NavigateToTutorialFragment : TodayEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String) : TodayEvent()
        data class ShowJEntrySavedConfirmationMessage(val msg: String) : TodayEvent()
        data class ShowTaskSavedInNewOrOldSetConfirmationMessage(val msg: String?) : TodayEvent()
        data class ShowTaskAddedFromSetConfirmationMessage(val msg: String?) : TodayEvent()
    }
}