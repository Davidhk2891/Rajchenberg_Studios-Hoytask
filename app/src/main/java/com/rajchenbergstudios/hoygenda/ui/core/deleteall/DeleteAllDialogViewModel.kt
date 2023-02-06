package com.rajchenbergstudios.hoygenda.ui.core.deleteall

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoygenda.data.day.DayDao
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
    private val dayDao: DayDao,
    state: SavedStateHandle,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel(){

    private val origin = state.get<Int>("origin")
    private val noDataMessage = "Nothing to delete"
    private val noCompletedTasksMessage = "You don't have any completed tasks"

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
            5 -> {deleteAllDays(resultInterface)}
        }
    }

    fun populateMessage(): String {
        var message = ""
        when (origin) {
            1 -> {message = "Do you really want to delete all completed tasks?"}
            2 -> {message = "Do you really want to delete all sets?"}
            3 -> {message = "Do you really want to delete all tasks?"}
            4 -> {message = "Do you really want to delete all entries?"}
            5 -> {message = "Do you really want to delete all days?"}
        }
        return message
    }

    private fun deleteAllCompletedTasks(resultInterface: ResultInterface) = applicationScope.launch{
        if (taskDao.checkCompletedTasksInList().isEmpty())
            resultInterface.onShowEmptyListMessage(noCompletedTasksMessage)
        else
            taskDao.deleteAllCompleted()
    }

    private fun deleteAllTasks(resultInterface: ResultInterface) = applicationScope.launch {
        if (taskDao.checkFirstItemFromList().isEmpty())
            resultInterface.onShowEmptyListMessage(noDataMessage)
        else
            taskDao.nukeTaskTable()
    }

    private fun deleteAllSetsWithTasks(resultInterface: ResultInterface) = applicationScope.launch {
        if (taskSetDao.firstItemFromList().isEmpty())
            resultInterface.onShowEmptyListMessage(noDataMessage)
        else {
            taskInSetDao.deleteAllTasksInSets()
            taskSetDao.deleteAllSets()
        }
    }

    private fun deleteAllJournalEntries(resultInterface: ResultInterface) = applicationScope.launch {
        if (journalEntryDao.firstItemFromList().isEmpty())
            resultInterface.onShowEmptyListMessage(noDataMessage)
        else
            journalEntryDao.nukeJEntryTable()
    }

    private fun deleteAllDays(resultInterface: ResultInterface) = applicationScope.launch {
        if (dayDao.checkFirstItemFromList().isEmpty())
            resultInterface.onShowEmptyListMessage(noDataMessage)
        else
            dayDao.nukeDayTable()
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