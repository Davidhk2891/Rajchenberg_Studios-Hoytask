package com.rajchenbergstudios.hoygenda.ui.today.todayjentrieslist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoygenda.data.prefs.PreferencesManager
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntry
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntryDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TJEntriesListViewModel @Inject constructor(
    private val journalEntryDao: JournalEntryDao,
    private val preferencesManager: PreferencesManager,
    state: SavedStateHandle
) : ViewModel() {

    val entries = journalEntryDao.getJournalEntries().asLiveData()

    // JEntries Channel
    private val jEntriesEventChannel = Channel<JEntriesEvent>()

    // JEntries Event
    val jEntriesEvent = jEntriesEventChannel.receiveAsFlow()

    fun onJEntrySelected(journalEntry: JournalEntry) = viewModelScope.launch {
        jEntriesEventChannel.send(JEntriesEvent.NavigateToEditJEntryScreen(journalEntry))
    }

    sealed class JEntriesEvent {
        data class NavigateToEditJEntryScreen(val journalEntry: JournalEntry) : JEntriesEvent()
    }
}