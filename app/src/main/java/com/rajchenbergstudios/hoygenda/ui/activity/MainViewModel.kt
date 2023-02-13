package com.rajchenbergstudios.hoygenda.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoygenda.data.day.Day
import com.rajchenbergstudios.hoygenda.data.day.DayDao
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntry
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntryDao
import com.rajchenbergstudios.hoygenda.data.today.task.Task
import com.rajchenbergstudios.hoygenda.data.today.task.TaskDao
import com.rajchenbergstudios.hoygenda.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

/*
 For testing only!
 const val TAG = "MainViewModel.kt"
 val localDateTest: LocalDate = LocalDate.of(2022, 11, 1)
 Logger.i(TAG, "pullListAndCompareDate", "Local date is: $localDateTest")
 Logger.i(TAG, "pullListAndCompareDate", "Local last task date is: $localLastTaskDate")
*/

const val TAG = "MainViewModel.kt"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val journalEntryDao: JournalEntryDao,
    private val dayDao: DayDao
) : ViewModel(){

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    var appWasAlreadyOpened = false

    // Channel
    private val mainEventChannel = Channel<MainEvent>()

    // Event
    val mainEvent = mainEventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            // Enable only when testing
            // pullListAndCompareDateTest()

            compareDateAndSaveDataIfNeeded()
            // You can do something here
            delay(1500)
            _isLoading.value = false
        }
    }

    private suspend fun compareDateAndSaveDataIfNeeded() {
        pullListCompareDatesAndSaveData()
    }

    fun compareDateAndSaveDataIfNeededInActivityResumed() = viewModelScope.launch {
        pullListCompareDatesAndSaveData()
    }

    private suspend fun pullListCompareDatesAndSaveData() {
        val lastTaskDateInMillis: Long
        val tasksList = taskDao.getTasksList()
        val jEntriesList = journalEntryDao.getJournalEntriesList()

        if (tasksList.isNotEmpty() || jEntriesList.isNotEmpty()) {

                if (tasksList.isNotEmpty()) {
                    lastTaskDateInMillis = tasksList.last().created
                    Logger.i(TAG, "pullListCompareDatesAndSaveData", "tasksList is not empty")
                } else {
                    lastTaskDateInMillis = jEntriesList.last().created
                    Logger.i(TAG, "pullListCompareDatesAndSaveData", "jEntriesList is not empty")
                }

            val localDate = LocalDate.now()
            val localLastTaskDate = Instant.ofEpochMilli(lastTaskDateInMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            if (localDate.isAfter(localLastTaskDate)) {
                Logger.i(TAG, "pullListCompareDatesAndSaveData", "It's a new day!")
                saveTasksAndJEntriesToDay(tasksList, jEntriesList, localLastTaskDate)
                nukeTodayTasks()
                nukeTodayJEntries()
            } else {
                Logger.i(TAG, "pullListCompareDatesAndSaveData", "We are still in Today")
            }
        } else {
            Logger.i(TAG, "pullListCompareDatesAndSaveData", "Nothing to do, both lists are empty")
        }
    }

    private fun saveTasksAndJEntriesToDay(tasksList: List<Task>, jEntriesList: List<JournalEntry>, tasksDate: LocalDate) = viewModelScope.launch {
        dayDao.insert(Day(
            tasksDate.dayOfWeek.toString(),
            tasksDate.dayOfMonth.toString(),
            tasksDate.month.toString(),
            tasksDate.year.toString(),
            tasksList,
            jEntriesList
        ))
    }

    private fun nukeTodayTasks() = viewModelScope.launch {
        taskDao.nukeTaskTable()
    }

    private fun nukeTodayJEntries() = viewModelScope.launch {
        journalEntryDao.nukeJEntryTable()
    }

    fun onTaskSetsListFragmentClick() = viewModelScope.launch {
        mainEventChannel.send(MainEvent.NavigateToTaskSetsListFragment)
    }

    fun onDaysListFragmentClick() = viewModelScope.launch {
        mainEventChannel.send(MainEvent.NavigateToDaysListFragment)
    }

    fun onGetInTouchDialogFragmentClick() = viewModelScope.launch {
        mainEventChannel.send(MainEvent.NavigateToGetInTouchDialog)
    }

    fun onLeaveReviewDialogFragmentClick() = viewModelScope.launch {
        mainEventChannel.send(MainEvent.NavigateToLeaveReviewDialog)
    }

    fun onChangelogDialogFragmentClick() = viewModelScope.launch {
        mainEventChannel.send(MainEvent.NavigateToChangelogDialog)
    }

    fun onTellYourFriendsDialogFragmentClick() = viewModelScope.launch {
        mainEventChannel.send(MainEvent.NavigateToTellYourFriendsDialog)
    }

    fun onAboutDialogFragmentClick() = viewModelScope.launch{
        mainEventChannel.send(MainEvent.NavigateToAboutDialog)
    }

    fun onTutorialRedirectionEngaged() = viewModelScope.launch {
        mainEventChannel.send(MainEvent.NavigateToTutorialFragment)
    }

    sealed class MainEvent {
        object NavigateToTaskSetsListFragment : MainEvent()
        object NavigateToDaysListFragment : MainEvent()
        object NavigateToGetInTouchDialog : MainEvent()
        object NavigateToLeaveReviewDialog : MainEvent()
        object NavigateToChangelogDialog : MainEvent()
        object NavigateToTellYourFriendsDialog : MainEvent()
        object NavigateToAboutDialog : MainEvent()
        object NavigateToTutorialFragment : MainEvent()
    }

    /*
    For testing only!
    private suspend fun pullListAndCompareDateTest() {
        val tasksList = taskDao.getTasksList()
        val jEntriesList = journalEntryDao.getJournalEntriesList()
        if (tasksList.isNotEmpty() || jEntriesList.isNotEmpty()) {

            val lastTaskDateInMillis: Long = tasksList.last().created
            val localLastTaskDate: LocalDate = Instant.ofEpochMilli(lastTaskDateInMillis).atZone(ZoneId.systemDefault()).toLocalDate()

            saveTasksAndJEntriesToDay(tasksList, jEntriesList, localLastTaskDate)
            nukeTodayTasks()
            nukeTodayJEntries()
        }
    }
     */
}