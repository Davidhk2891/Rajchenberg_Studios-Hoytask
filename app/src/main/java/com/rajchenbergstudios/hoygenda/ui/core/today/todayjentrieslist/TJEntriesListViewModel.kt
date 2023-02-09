package com.rajchenbergstudios.hoygenda.ui.core.today.todayjentrieslist

import androidx.lifecycle.*
import com.rajchenbergstudios.hoygenda.data.prefs.PreferencesManager
import com.rajchenbergstudios.hoygenda.data.prefs.SortOrder
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntry
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntryDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class TJEntriesListViewModel @Inject constructor(
    private val journalEntryDao: JournalEntryDao,
    private val preferencesManager: PreferencesManager,
    state: SavedStateHandle
) : ViewModel() {

    val searchQuery = state.getLiveData("jEntriesSearchQuery", "")

    // DataStore
    private val preferencesFlow = preferencesManager.todayPreferencesFlow

    private val jEntriesFlow = combine(
        searchQuery.asFlow(),
        preferencesFlow
    ) { searchQuery, filterPreferences ->
        Pair(searchQuery, filterPreferences)
    }.flatMapLatest { (searchQuery, filterPreferences) ->
        journalEntryDao.getJournalEntries(searchQuery, filterPreferences.sortOrder)
    }

    // JEntries Channel
    private val jEntriesEventChannel = Channel<JEntriesEvent>()

    // JEntries Event
    val jEntriesEvent = jEntriesEventChannel.receiveAsFlow()

    fun onJEntrySelected(journalEntry: JournalEntry) = viewModelScope.launch {
        jEntriesEventChannel.send(JEntriesEvent.NavigateToEditJEntryScreen(journalEntry))
    }

    fun onJEntrySwiped(journalEntry: JournalEntry) = viewModelScope.launch {
        journalEntryDao.delete(journalEntry)
        jEntriesEventChannel.send(JEntriesEvent.ShowUndoDeleteJEntryMessage(journalEntry))
    }

    fun onUndoDeleteClick(journalEntry: JournalEntry) = viewModelScope.launch {
        journalEntryDao.insert(journalEntry)
    }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onDeleteAllClick() = viewModelScope.launch {
        jEntriesEventChannel.send(JEntriesEvent.NavigateToDeleteAllScreen)
    }

    val entries = jEntriesFlow.asLiveData()

    sealed class JEntriesEvent {
        object NavigateToDeleteAllScreen : JEntriesEvent()
        data class ShowUndoDeleteJEntryMessage(val journalEntry: JournalEntry) : JEntriesEvent()
        data class NavigateToEditJEntryScreen(val journalEntry: JournalEntry) : JEntriesEvent()
    }
}