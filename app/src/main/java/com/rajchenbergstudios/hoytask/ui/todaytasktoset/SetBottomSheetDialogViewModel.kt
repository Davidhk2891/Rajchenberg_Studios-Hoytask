package com.rajchenbergstudios.hoytask.ui.todaytasktoset

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoytask.data.task.Task
import com.rajchenbergstudios.hoytask.data.taskinset.TaskInSet
import com.rajchenbergstudios.hoytask.data.taskinset.TaskInSetDao
import com.rajchenbergstudios.hoytask.data.taskset.TaskSet
import com.rajchenbergstudios.hoytask.data.taskset.TaskSetDao
import com.rajchenbergstudios.hoytask.di.ApplicationScope
import com.rajchenbergstudios.hoytask.ui.CREATE_SET_RESULT_OK
import com.rajchenbergstudios.hoytask.ui.EDIT_SET_RESULT_OK
import com.rajchenbergstudios.hoytask.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "SetBottomSheetDialogViewModel.kt"

@HiltViewModel
class TaskToSetBottomSheetDialogViewModel @Inject constructor(
    private val taskSetDao: TaskSetDao,
    private val taskInSetDao: TaskInSetDao,
    @ApplicationScope private val applicationScope: CoroutineScope,
    state: SavedStateHandle
) : ViewModel(){

    val task = state.get<Task>("task")

    private val taskName = state.get<String>("taskName") ?: task?.name

    private val setsTitles: ArrayList<String> = ArrayList()

    // TaskToSet Channel
    private val taskToSetChannel = Channel<TaskToSetEvent>()

    // TaskToSet Event
    val taskToSetEvent = taskToSetChannel.receiveAsFlow()

    val taskSets = taskSetDao.getSets().asLiveData()

    fun onCreateSetClicked() = viewModelScope.launch {
        taskToSetChannel.send(TaskToSetEvent.NavigateToCreateTaskSetDialog(task))
    }

    fun onCreateSetResult(result: Int) = viewModelScope.launch {
        when (result) {
            CREATE_SET_RESULT_OK -> navigateBackWithResultFromSetCreatedWithTask(result)
        }
    }

    fun onDoneClicked() {
        if (setsTitles.isEmpty()){
            navigateBackWithNoSetsSelected()
            return
        }
        onSaveTaskToSets()
    }

    private fun onSaveTaskToSets() = viewModelScope.launch {
        if (taskName != null) {
            for (setTitle in setsTitles) {
                taskInSetDao.insert(TaskInSet(taskName, setTitle))
            }
            if (setsTitles.size > 1)
                showTaskAddedtoExistingSetMessage("Task added to sets")
            else
                showTaskAddedtoExistingSetMessage("Task added to set")
        }
    }

    fun holdDataToSave(taskSet: TaskSet, isChecked: Boolean) {
        onTaskSetCheckedChanged(taskSet, isChecked)
        if (isChecked)
            setsTitles.add(taskSet.title)
        else
            setsTitles.remove(taskSet.title)

        Logger.i(TAG, "holdDataToSave", setsTitles.toString())
    }

    private fun navigateBackWithResultFromSetCreatedWithTask(result: Int) = viewModelScope.launch {
        taskToSetChannel.send(TaskToSetEvent.NavigateBackWithResultFromSetCreatedWithTask(result))
    }

    private fun navigateBackWithNoSetsSelected() = viewModelScope.launch {
        taskToSetChannel.send(TaskToSetEvent.NavigateBackWithNoSetsSelected)
    }

    private fun showTaskAddedtoExistingSetMessage(msg: String) = viewModelScope.launch {
        taskToSetChannel.send(TaskToSetEvent.NaigateBackWithResultUponSavingTaskToSet(EDIT_SET_RESULT_OK, msg))
    }

    private fun onTaskSetCheckedChanged(taskSet: TaskSet, isChecked: Boolean) = viewModelScope.launch {
        taskSetDao.update(taskSet.copy(chosen = isChecked))
    }

    fun onClearChosenStatus() = applicationScope.launch {
        setsTitles.clear()
        for (taskSet in taskSetDao.getChosenSets(true)) {
            taskSetDao.update(taskSet.copy(chosen = false))
        }
    }

    sealed class TaskToSetEvent {
        object NavigateBackWithNoSetsSelected : TaskToSetEvent()
        data class NaigateBackWithResultUponSavingTaskToSet(val result: Int, val msg: String) : TaskToSetEvent()
        data class NavigateToCreateTaskSetDialog(val task: Task?) : TaskToSetEvent()
        data class NavigateBackWithResultFromSetCreatedWithTask(val result: Int) : TaskToSetEvent()
    }
}