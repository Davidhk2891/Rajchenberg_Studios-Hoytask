package com.rajchenbergstudios.hoygenda.ui.todaylists.journalentrieslist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rajchenbergstudios.hoygenda.data.prefs.PreferencesManager
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntryDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class JEntriesListViewModel @Inject constructor(
    private val journalEntryDao: JournalEntryDao,
    private val preferencesManager: PreferencesManager,
    state: SavedStateHandle
) : ViewModel() {

    val entries = journalEntryDao.getJournalEntries().asLiveData()
}