package com.rajchenbergstudios.hoygenda.ui.core.jentryaddedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntry
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntryDao
import com.rajchenbergstudios.hoygenda.ui.activity.ADD_JENTRY_RESULT_OK
import com.rajchenbergstudios.hoygenda.ui.activity.EDIT_JENTRY_RESULT_OK
import com.rajchenbergstudios.hoygenda.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "JEntryAddEditViewModel.kt"

@HiltViewModel
class JEntryAddEditViewModel @Inject constructor(
    private val journalEntryDao: JournalEntryDao,
    private val state: SavedStateHandle
) : ViewModel() {

    val origin = state.get<Int>("origin")

    val jentry = state.get<JournalEntry>("jentry")

    var jentryText = state.get<String>("jentryText") ?: jentry?.content ?: ""
        set(value) {
            field = value
            state["jentryText"] = value
        }

    var jentryImportance = state.get<Boolean>("jentryImportance") ?: jentry?.important ?: false
        set(value) {
            field = value
            state["jentryImportance"] = value
        }

    // JEAddEdit event channel
    private val jeAddEditEventChannel = Channel<JEAddEditEvent>()

    //JEAddEdit event
    val jeAddEditEvent = jeAddEditEventChannel.receiveAsFlow()

    fun onSaveClick() {
        createOrUpdateJournalEntry()
    }

    fun deduceFlow() = viewModelScope.launch {
        when (origin) {
            1 -> {jeAddEditEventChannel.send(JEAddEditEvent.ShowFlowFromJEntriesList)}
            2 -> {jeAddEditEventChannel.send(JEAddEditEvent.ShowFlowFromPastDayJEntriesList)}
        }
    }

    private fun createOrUpdateJournalEntry() {
        Logger.i(TAG, "createOrUpdateJournalEntry", "is $jentryText")
        if (jentryText.isBlank()) {
            showInvalidInputMessage()
            return
        }
        if (jentry != null) {
            val updatedJournalEntry = jentry.copy(content = jentryText, important = jentryImportance)
            updateJEntry(updatedJournalEntry)
        } else {
            val newJournalEntry = JournalEntry(content = jentryText, important = jentryImportance)
            createJEntry(newJournalEntry)
        }
    }

    private fun createJEntry(journalEntry: JournalEntry) = viewModelScope.launch {
        journalEntryDao.insert(journalEntry)
        jeAddEditEventChannel.send(JEAddEditEvent.NavigateBackWithResult(ADD_JENTRY_RESULT_OK))
    }

    private fun updateJEntry(journalEntry: JournalEntry) = viewModelScope.launch {
        journalEntryDao.update(journalEntry)
        jeAddEditEventChannel.send(JEAddEditEvent.NavigateBackWithResult(EDIT_JENTRY_RESULT_OK))
    }

    private fun showInvalidInputMessage() = viewModelScope.launch {
        jeAddEditEventChannel.send(JEAddEditEvent.ShowInvalidInputMessage("Content cannot be empty"))
    }

    sealed class JEAddEditEvent {
        object ShowFlowFromJEntriesList : JEAddEditEvent()
        object ShowFlowFromPastDayJEntriesList : JEAddEditEvent()
        data class NavigateBackWithResult(val result: Int) : JEAddEditEvent()
        data class ShowInvalidInputMessage(val message: String) : JEAddEditEvent()
    }
}