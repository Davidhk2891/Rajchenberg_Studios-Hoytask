package com.rajchenbergstudios.hoygenda.ui.core.deleteall

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoygenda.data.today.task.TaskDao
import com.rajchenbergstudios.hoygenda.data.taskinset.TaskInSetDao
import com.rajchenbergstudios.hoygenda.data.taskset.TaskSetDao
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntryDao
import com.rajchenbergstudios.hoygenda.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllDialogViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val taskSetDao: TaskSetDao,
    private val taskInSetDao: TaskInSetDao,
    private val journalEntryDao: JournalEntryDao,
    state: SavedStateHandle,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel(){

    private val origin = state.get<Int>("origin")
    private val message = "Nothing to delete"

    // DeleteAll channel
    private val deleteAllEventChannel = Channel<DeleteAllEvent>()

    // DeleteAll Event
    val deleteAllEvent = deleteAllEventChannel.receiveAsFlow()

    fun onConfirmClick(resultInterface: ResultInterface) {
        when (origin) {
            1 -> {deleteAllCompletedTasks(resultInterface)}
            2 -> {deleteAllSetsWithTasks(resultInterface)}
            3 -> {deleteAllTasks(resultInterface)}
            4 -> {deleteAllJournalEntries(resultInterface)}
        }
    }

    private fun deleteAllCompletedTasks(resultInterface: ResultInterface) = applicationScope.launch{
        if (taskDao.firstItemFromList().isEmpty())
            resultInterface.onShowEmptyListMessage(message)
        else
            taskDao.deleteAllCompleted()
    }

    private fun deleteAllTasks(resultInterface: ResultInterface) = applicationScope.launch {
        if (taskDao.firstItemFromList().isEmpty())
            resultInterface.onShowEmptyListMessage(message)
        else
            taskDao.nukeTaskTable()
    }

    private fun deleteAllSetsWithTasks(resultInterface: ResultInterface) = applicationScope.launch {
        if (taskSetDao.firstItemFromList().isEmpty())
            resultInterface.onShowEmptyListMessage(message)
        else {
            taskInSetDao.deleteAllTasksInSets()
            taskSetDao.deleteAllSets()
        }
    }

    private fun deleteAllJournalEntries(resultInterface: ResultInterface) = applicationScope.launch {
        if (journalEntryDao.firstItemFromList().isEmpty())
            resultInterface.onShowEmptyListMessage(message)
        else
            journalEntryDao.nukeJEntryTable()
    }

    fun populateMessage(): String {
        var message = ""
        when (origin) {
            1 -> {message = "Do you really want to delete all completed tasks?"}
            2 -> {message = "Do you really want to delete all sets?"}
            3 -> {message = "Do you really want to delete all tasks?"}
            4 -> {message = "Do you really want to delete all entries?"}
        }
        return message
    }

    fun showNothingToDeleteMessage(message: String) = viewModelScope.launch {
        deleteAllEventChannel.send(DeleteAllEvent.ShowNothingToDeleteMessage(message))
    }

    interface ResultInterface {
        fun onShowEmptyListMessage(message: String)
    }

    sealed class DeleteAllEvent {
        data class ShowNothingToDeleteMessage(val message: String) : DeleteAllEvent()
    }
}