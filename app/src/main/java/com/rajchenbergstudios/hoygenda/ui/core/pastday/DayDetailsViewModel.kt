package com.rajchenbergstudios.hoygenda.ui.core.pastday

import androidx.lifecycle.*
import com.rajchenbergstudios.hoygenda.data.day.Day
import com.rajchenbergstudios.hoygenda.data.prefs.SortOrder
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntry
import com.rajchenbergstudios.hoygenda.data.today.task.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "DayDetailsViewModel"

@FlowPreview
@ExperimentalCoroutinesApi
@HiltViewModel
class DayDetailsViewModel @Inject constructor(
    stateHandle: SavedStateHandle
) : ViewModel(){

    // DayDetailsFragment operations ---

    // This is the reason why this is a shared viewModel.
    // The data comes from this day value below.
    // Past from the selected day in Days list.
    val day = stateHandle.get<Day>("day")

    val dayWeekDay = stateHandle["weekDay"] ?: day?.dayOfWeek ?: "null"

    val dayMonth = stateHandle["month"] ?: day?.month ?: "null"

    val dayYear = stateHandle["year"] ?: day?.year ?: "null"

    val dayMonthDay = stateHandle["monthDay"] ?: day?.dayOfMonth ?: "null"

    var formattedDate: String = ""

    init {
        formattedDate = "${formatMonth(dayMonth)} $dayMonthDay, $dayYear"
    }

    private fun formatMonth(month: String): String {
        val dayMonthLowercase = month.lowercase()
        val dayMonthFirstLetterCap = dayMonthLowercase.replaceFirst(
            dayMonthLowercase.first().toString()
            ,dayMonthLowercase.first().uppercase()
        )
        return dayMonthFirstLetterCap.substring(0, 3)
    }

    // PDTasksListFragment/PDJEntriesListFragment operations ---

    // Search query flow for tasks
    val pastDaySearchQuery = MutableStateFlow("")
    // Search query flow for journal entries
    val pastDaySearchQueryJEntries = MutableStateFlow("")

    // TODO /////////////////////////////////////////////////////////////////
    // Sort order flow for tasks
    val pastDaySortOrderQuery = MutableStateFlow(SortOrder.BY_TIME)
    // Sort order flow for journal entries
    val pastDaySortOrderQueryJEntries = MutableStateFlow(SortOrder.BY_TIME)

    // Data source tasks
    val tasksList: List<Task>? = day?.listOfTasks
    // Data source journal entries
    val jEntriesList: List<JournalEntry>? = day?.listOfJEntries

    // Converted tasks data source to Flow
    private val tasksListFlow: Flow<List<Task>>? = tasksList?.let { tasksList -> flowOf(tasksList) }
    // Converted journal entries data source to Flow
    private val jEntriesListFlow: Flow<List<JournalEntry>>? = jEntriesList?.let { jEntriesList -> flowOf(jEntriesList) }

    // TASKS ---

//    Combined flow from pastDaySearchQuery and pastDaySortOrderQuery
//    private val combinedFlow = tasksListFlow?.flatMapMerge { tasks ->
//        pastDaySearchQuery.flatMapLatest { query ->
//            pastDaySortOrderQuery.flatMapLatest { sortBy ->
//                val filteredTasks = tasks.filter { task ->
//                    task.title.contains(query, ignoreCase = true)
//                }
//                flowOf(
//                    when (sortBy) {
//                        SortOrder.BY_TIME -> filteredTasks.sortedBy { it.createdTimeFormat }
//                        SortOrder.BY_NAME -> filteredTasks.sortedBy { it.title }
//                    }
//                )
//            }
//        }
//    }

    private val filteredTasksFlow = tasksListFlow?.flatMapMerge { tasks ->
        pastDaySearchQuery.flatMapLatest { query ->
            flowOf(tasks.filter { task ->
                task.title.contains(query, ignoreCase = true)
            })
            // Nothing can be written here
        }
    }

    // Expose the flow as LiveData to the Fragment so it can be observed
    val tasks = filteredTasksFlow?.asLiveData()

    // ---------

    // JOURNAL ENTRIES-

    // Apply searchQuery to jEntriesListFlow and filter in the result that match the query
    private val filteredJEntriesFlow = jEntriesListFlow?.flatMapMerge { jEntries ->
        pastDaySearchQueryJEntries.flatMapLatest { query ->
            flowOf(jEntries.filter { jEntry ->
                jEntry.content.contains(query, ignoreCase = true)
            })
            // Nothing can be written here
        }
    }

    // Apply sortOrder to filteredJEntriesFlow and sort the list bt the requested sorting
//    private val sortedJEntriesFlow = filteredJEntriesFlow?.flatMapLatest { jEntries ->
//        pastDaySortOrderQueryJEntries.map { sortBy ->
//            when (sortBy) {
//                SortOrder.BY_TIME -> jEntries.sortedBy { it.createdTimeFormat }
//                SortOrder.BY_NAME -> jEntries.sortedBy { it.content }
//            }
//        }
//    }

    // Expose the flow as LiveData to the Fragment so it can be observed
    val jEntries = filteredJEntriesFlow?.asLiveData()

    // ---------


    // ----------------------------------------------------------------------

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
        pastDayJEntryEventChannel.send(
            PastDayJEntryEvent.NavigateToJEntryDetailsScreen(
                journalEntry,
                date
            )
        )
    }

    // Past day Task & JEntry Events wrapper class
    sealed class PastDayTaskEvent {
        data class NavigateToTaskDetailsScreen(val task: Task, val date: String) : PastDayTaskEvent()
    }

    sealed class PastDayJEntryEvent {
        data class NavigateToJEntryDetailsScreen(val journalEntry: JournalEntry, val date: String) : PastDayJEntryEvent()
    }


}