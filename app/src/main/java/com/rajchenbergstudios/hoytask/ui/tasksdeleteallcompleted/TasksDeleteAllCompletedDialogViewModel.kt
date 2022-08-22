package com.rajchenbergstudios.hoytask.ui.tasksdeleteallcompleted

import androidx.lifecycle.ViewModel
import com.rajchenbergstudios.hoytask.data.task.TaskDao
import com.rajchenbergstudios.hoytask.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksDeleteAllCompletedDialogViewModel @Inject constructor(
    private val taskDao: TaskDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel(){

    fun onConfirmClick() = applicationScope.launch {
        taskDao.deleteAllCompleted()
    }
}