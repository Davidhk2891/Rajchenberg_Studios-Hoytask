package com.rajchenbergstudios.hoygenda.ui.deleteall

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoygenda.data.today.TodayDao
import com.rajchenbergstudios.hoygenda.data.taskinset.TaskInSetDao
import com.rajchenbergstudios.hoygenda.data.taskset.TaskSetDao
import com.rajchenbergstudios.hoygenda.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllDialogViewModel @Inject constructor(
    private val todayDao: TodayDao,
    private val taskSetDao: TaskSetDao,
    private val taskInSetDao: TaskInSetDao,
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
        }
    }

    private fun deleteAllCompletedTasks(resultInterface: ResultInterface) = applicationScope.launch{
        if (todayDao.firstItemFromList().isEmpty())
            resultInterface.onShowEmptyListMessage(message)
        else
            todayDao.deleteAllCompleted()
    }

    private fun deleteAllTasks(resultInterface: ResultInterface) = applicationScope.launch {
        if (todayDao.firstItemFromList().isEmpty())
            resultInterface.onShowEmptyListMessage(message)
        else
            todayDao.nukeTaskTable()
    }

    private fun deleteAllSetsWithTasks(resultInterface: ResultInterface) = applicationScope.launch {
        if (taskSetDao.firstItemFromList().isEmpty())
            resultInterface.onShowEmptyListMessage(message)
        else {
            taskInSetDao.deleteAllTasksInSets()
            taskSetDao.deleteAllSets()
        }
    }

    fun populateMessage(): String {
        var message = ""
        when (origin) {
            1 -> {message = "Do you really want to delete all completed tasks?"}
            2 -> {message = "Do you really want to delete all sets?"}
            3 -> {message = "Do you really want to delete all tasks?"}
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