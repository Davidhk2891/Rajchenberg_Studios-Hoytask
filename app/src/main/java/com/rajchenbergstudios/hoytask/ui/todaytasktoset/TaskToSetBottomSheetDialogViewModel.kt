package com.rajchenbergstudios.hoytask.ui.todaytasktoset

import androidx.lifecycle.ViewModel
import com.rajchenbergstudios.hoytask.data.task.TaskDao
import com.rajchenbergstudios.hoytask.data.taskinset.TaskInSetDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaskToSetBottomSheetDialogViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val taskInSetDao: TaskInSetDao
) : ViewModel(){

    
}