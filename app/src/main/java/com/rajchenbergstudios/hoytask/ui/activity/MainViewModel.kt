package com.rajchenbergstudios.hoytask.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoytask.data.day.Day
import com.rajchenbergstudios.hoytask.data.day.DayDao
import com.rajchenbergstudios.hoytask.data.task.Task
import com.rajchenbergstudios.hoytask.data.task.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

// const val TAG = "MainViewModel.kt"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val dayDao: DayDao
) : ViewModel(){

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            pullListAndCompareDate()
            delay(1500)
            _isLoading.value = false
        }
    }

    private suspend fun pullListAndCompareDate() {
        val tasksList = taskDao.getTasks()
        if (tasksList.isNotEmpty()) {
            val lastTaskDateInMillis = tasksList.last().created
            val localDate = LocalDate.now()
            val localLastTaskDate = Instant.ofEpochMilli(lastTaskDateInMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            // val localDateTest: LocalDate = LocalDate.of(2022, 11, 1)
            // Logger.i(TAG, "pullListAndCompareDate", "Local date is: $localDateTest")
            // Logger.i(TAG, "pullListAndCompareDate", "Local last task date is: $localLastTaskDate")
            if (localDate.isAfter(localLastTaskDate)) {
                saveTasksToDay(tasksList, localLastTaskDate)
                nukeTodayTasks()
            }
        }
    }

    private fun saveTasksToDay(tasksList: List<Task>, tasksDate: LocalDate) = viewModelScope.launch {
        dayDao.insert(Day(
            tasksDate.dayOfWeek.toString(),
            tasksDate.dayOfMonth.toString(),
            tasksDate.month.toString(),
            tasksDate.year.toString(),
            tasksList
        ))
    }

    private fun nukeTodayTasks() = viewModelScope.launch {
        taskDao.nukeTaskTable()
    }
}