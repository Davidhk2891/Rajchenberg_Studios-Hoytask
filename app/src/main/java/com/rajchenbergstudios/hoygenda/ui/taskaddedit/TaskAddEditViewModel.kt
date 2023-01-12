package com.rajchenbergstudios.hoygenda.ui.taskaddedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoygenda.data.today.task.Task
import com.rajchenbergstudios.hoygenda.data.today.task.TaskDao
import com.rajchenbergstudios.hoygenda.data.taskinset.TaskInSet
import com.rajchenbergstudios.hoygenda.data.taskinset.TaskInSetDao
import com.rajchenbergstudios.hoygenda.ui.activity.ADD_TASK_IN_SET_OK
import com.rajchenbergstudios.hoygenda.ui.activity.ADD_TASK_RESULT_OK
import com.rajchenbergstudios.hoygenda.ui.activity.EDIT_TASK_IN_SET_OK
import com.rajchenbergstudios.hoygenda.ui.activity.EDIT_TASK_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// private const val TAG = "TaskEditViewModel.kt"

@HiltViewModel
class TaskAddEditViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val taskInSetDao: TaskInSetDao,
    private val state: SavedStateHandle
) : ViewModel() {

    val origin = state.get<Int>("origin")

    val mTask = state.get<Task>("task")

    var taskName = state.get<String>("taskName") ?: mTask?.title ?: ""
        set(value) {
            field = value
            state["taskName"] = value
        }

    var taskImportance = state.get<Boolean>("taskImportance") ?: mTask?.important ?: false
        set(value) {
            field = value
            state["taskName"] = value
        }

    val taskInSet = state.get<TaskInSet>("taskinset")

    var taskInSetName = state.get<String>("taskInSetName") ?: taskInSet?.taskInSet ?: ""
        set(value) {
            field = value
            state["taskInSetName"] = value
        }

    private var taskInSetBigTitle = state.get<String>("taskInSetBigTitle") ?: taskInSet?.taskInSetBigTitle ?: ""
        set(value) {
            field = value
            state["taskInSetBigTitle"] = value
        }

    // AddEdit event channel
    private val addEditEventChannel = Channel<AddEditEvent>()

    // AddEdit event
    val addEditEvent = addEditEventChannel.receiveAsFlow()

    private var isNewTask: Boolean = false

    init {
        if (taskInSetName.isBlank() || taskInSetName.isEmpty()) {
            isNewTask = true
        }
    }

    fun onSaveClick(){
        when (origin) {
            1 -> createOrUpdateTask()
            2 -> createOrUpdateTaskInSet()
        }
    }

    fun deduceFlow() = viewModelScope.launch {
        when (origin) {
            1 -> {addEditEventChannel.send(AddEditEvent.ShowFlowFromTaskList)}
            2 -> {addEditEventChannel.send(AddEditEvent.ShowFlowFromTaskInSetList)}
            3 -> {addEditEventChannel.send(AddEditEvent.ShowFlowFromPastDayTaskList)}
        }
    }

    private fun createOrUpdateTask() {
        if (taskName.isBlank()) {
            showInvalidInputMessage()
            return
        }
        if (mTask != null) {
            val updatedTask = mTask.copy(title = taskName, important = taskImportance)
            updateTask(updatedTask)
        } else {
            val newTask = Task(title = taskName, important = taskImportance)
            createTask(newTask)
        }
    }

    private fun createOrUpdateTaskInSet() {
        if (taskInSetName.isBlank()) {
            showInvalidInputMessage()
            return
        }
        if (taskInSet != null) {
            if (!isNewTask) {
                val updatedTaskInSet =
                    taskInSet.copy(taskInSetBigTitle = taskInSetBigTitle, taskInSet = taskInSetName)
                updateTaskInSet(updatedTaskInSet)
            } else {
                val newTaskInSet =
                    TaskInSet(taskInSetBigTitle = taskInSetBigTitle, taskInSet = taskInSetName)
                createTaskInSet(newTaskInSet)
            }
        }
    }

    private fun createTask(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
        addEditEventChannel.send(AddEditEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updateTask(task: Task) = viewModelScope.launch {
        taskDao.update(task)
        addEditEventChannel.send(AddEditEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    private fun createTaskInSet(taskInSet: TaskInSet) = viewModelScope.launch {
        taskInSetDao.insert(taskInSet)
        addEditEventChannel.send(AddEditEvent.NavigateBackWithResult(ADD_TASK_IN_SET_OK))
    }

    private fun updateTaskInSet(taskInSet: TaskInSet) = viewModelScope.launch {
        taskInSetDao.update(taskInSet)
        addEditEventChannel.send(AddEditEvent.NavigateBackWithResult(EDIT_TASK_IN_SET_OK))
    }

    private fun showInvalidInputMessage() = viewModelScope.launch {
        addEditEventChannel.send(AddEditEvent.ShowInvalidInputMessage("Name cannot be empty"))
    }

    sealed class AddEditEvent {
        object ShowFlowFromTaskList : AddEditEvent()
        object ShowFlowFromTaskInSetList : AddEditEvent()
        object ShowFlowFromPastDayTaskList : AddEditEvent()
        data class ShowInvalidInputMessage(val msg: String) : AddEditEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditEvent()
    }
}