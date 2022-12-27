package com.rajchenbergstudios.hoygenda.ui.pastday

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoygenda.data.day.Day
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntry
import com.rajchenbergstudios.hoygenda.data.today.task.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedDayDetailsViewModel @Inject constructor(
    stateHandle: SavedStateHandle
) : ViewModel(){

    // DayDetailsFragment operations ---

    val day = stateHandle.get<Day>("day")

    val dayWeekDay = stateHandle["weekDay"] ?: day?.dayOfWeek ?: "null"

    val dayMonth = stateHandle["month"] ?: day?.month ?: "null"

    val dayYear = stateHandle["year"] ?: day?.year ?: "null"

    val dayMonthDay = stateHandle["monthDay"] ?: day?.dayOfMonth ?: "null"

    val tasksList = day?.listOfTasks

    val jEntriesList = day?.listOfJEntries

    var formattedDate: String = ""

    init {
        formattedDate = "${formatmonth(dayMonth)} $dayMonthDay, $dayYear"
    }

    private fun formatmonth(month: String): String {
        val dayMonthLowercase = month.lowercase()
        val dayMonthFirstLetterCap = dayMonthLowercase.replaceFirst(
            dayMonthLowercase.first().toString()
            ,dayMonthLowercase.first().uppercase()
        )
        return dayMonthFirstLetterCap.substring(0, 3)
    }

    // PDTasksListFragment/PDJEntriesListFragment operations ---

    // Past day Task & JEntries Channels
    private val pastDayTaskEventChannel = Channel<PastDayTaskEvent>()
    private val pastDayJEntryEventChannel = Channel<PastDayJEntryEvent>()

    // Past day Task & JEntries events
    val pastDayTaskEvent = pastDayTaskEventChannel.receiveAsFlow()
    val pastDayJEntryEvent = pastDayJEntryEventChannel.receiveAsFlow()

    // Past day Task & JEntries functions with Coroutines
    fun onPastDayTaskClick(task: Task, date: String) = viewModelScope.launch {
        pastDayTaskEventChannel.send(PastDayTaskEvent.NavigateToTaskDetailsScreen(task, date))
    }

    fun onPastDayJEntryClick(journalEntry: JournalEntry, date: String) = viewModelScope.launch {
        pastDayJEntryEventChannel.send(PastDayJEntryEvent.NavigateToJEntryDetailsScreen(journalEntry, date))
    }

    // Past day Task & JEntry Events wrapper class
    sealed class PastDayTaskEvent {
        data class NavigateToTaskDetailsScreen(val task: Task, val date: String) : PastDayTaskEvent()
    }

    sealed class PastDayJEntryEvent {
        data class NavigateToJEntryDetailsScreen(val journalEntry: JournalEntry, val date: String) : PastDayJEntryEvent()
    }


}