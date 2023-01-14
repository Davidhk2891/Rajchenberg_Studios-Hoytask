package com.rajchenbergstudios.hoygenda.ui.core.todaytasktoset

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoygenda.data.today.task.Task
import com.rajchenbergstudios.hoygenda.data.today.task.TaskDao
import com.rajchenbergstudios.hoygenda.data.taskinset.TaskInSet
import com.rajchenbergstudios.hoygenda.data.taskinset.TaskInSetDao
import com.rajchenbergstudios.hoygenda.data.taskset.TaskSet
import com.rajchenbergstudios.hoygenda.data.taskset.TaskSetDao
import com.rajchenbergstudios.hoygenda.di.ApplicationScope
import com.rajchenbergstudios.hoygenda.ui.activity.ADD_TASK_FROM_SET_RESULT_OK
import com.rajchenbergstudios.hoygenda.ui.activity.CREATE_SET_RESULT_OK
import com.rajchenbergstudios.hoygenda.ui.activity.EDIT_SET_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// const val TAG = "SetBottomSheetDialogViewModel.kt"

@HiltViewModel
class TaskToSetBottomSheetDialogViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val taskSetDao: TaskSetDao,
    private val taskInSetDao: TaskInSetDao,
    @ApplicationScope private val applicationScope: CoroutineScope,
    state: SavedStateHandle
) : ViewModel(){

    val mTask = state.get<Task>("task")

    val origin = state.get<Int>("origin")

    private val taskName = state.get<String>("taskName") ?: mTask?.title

    private val setsTitles: ArrayList<String> = ArrayList()

    // TaskToSet Channel
    private val taskToSetChannel = Channel<TaskToSetEvent>()

    // TaskToSet Event
    val taskToSetEvent = taskToSetChannel.receiveAsFlow()

    val taskSets = taskSetDao.getSets().asLiveData()

    fun onCreateSetClicked() = viewModelScope.launch {
        taskToSetChannel.send(TaskToSetEvent.NavigateToCreateTaskSetDialog(mTask))
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
        when (origin) {
            1 -> {onSaveTaskToSets()}
            2 -> {onFetchTasksFromSets()}
        }
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

    private fun onFetchTasksFromSets() = viewModelScope.launch {
        var multipleTasks = false
        var conditionChecked = false
        for (setTitle in setsTitles) {
            for (task in taskInSetDao.getTasksFromSet(setTitle)) {
                taskDao.insert(Task(task.taskInSet))
                if (!conditionChecked){
                    if (taskInSetDao.getTasksFromSet(setTitle).size > 1) {
                        multipleTasks = true
                        conditionChecked = true
                    }
                }
            }
        }
        if (setsTitles.size > 1) {
            if (multipleTasks)
                showTaskAddedFromExistingSetMessage("Tasks added from sets")
            else
                showTaskAddedFromExistingSetMessage("Task added from sets")
        } else {
            if (multipleTasks)
                showTaskAddedFromExistingSetMessage("Tasks added from set")
            else
                showTaskAddedFromExistingSetMessage("Task added from set")
        }
    }

    fun holdDataToSave(taskSet: TaskSet, isChecked: Boolean) {
        onTaskSetCheckedChanged(taskSet, isChecked)
        if (isChecked)
            setsTitles.add(taskSet.title)
        else
            setsTitles.remove(taskSet.title)
    }

    private fun navigateBackWithResultFromSetCreatedWithTask(result: Int) = viewModelScope.launch {
        taskToSetChannel.send(TaskToSetEvent.NavigateBackWithResultFromSetCreatedWithTask(result))
    }

    private fun navigateBackWithNoSetsSelected() = viewModelScope.launch {
        taskToSetChannel.send(TaskToSetEvent.NavigateBackWithNoSetsSelected)
    }

    private fun showTaskAddedtoExistingSetMessage(msg: String) = viewModelScope.launch {
        taskToSetChannel.send(
            TaskToSetEvent.NaigateBackWithResultUponSavingTaskToSet(
                EDIT_SET_RESULT_OK,
                msg
            )
        )
    }

    private fun showTaskAddedFromExistingSetMessage(msg: String) = viewModelScope.launch {
        taskToSetChannel.send(
            TaskToSetEvent.NavigateBackWithResultUponAddingTasksFromSet(
                ADD_TASK_FROM_SET_RESULT_OK, msg
            )
        )
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
        data class NavigateBackWithResultUponAddingTasksFromSet(val result: Int, val msg: String) : TaskToSetEvent()
        data class NavigateToCreateTaskSetDialog(val task: Task?) : TaskToSetEvent()
        data class NavigateBackWithResultFromSetCreatedWithTask(val result: Int) : TaskToSetEvent()
    }
}