package com.rajchenbergstudios.hoytask.ui.taskslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rajchenbergstudios.hoytask.data.task.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class TasksListViewModel @Inject constructor(
    private val taskDao: TaskDao
) : ViewModel(){

    val searchQuery = MutableStateFlow("")

    val sortOrderQuery = MutableStateFlow(SortOrder.BY_DATE)

    val hideCompletedQuery = MutableStateFlow(false)

    private val tasksFlow = combine(
        searchQuery,
        sortOrderQuery,
        hideCompletedQuery
    ) { searchQuery, sortOrderQuery, hideCompletedQuery ->
        Triple(searchQuery, sortOrderQuery, hideCompletedQuery)
    }.flatMapLatest { (searchQuery, sortOrderQuery, hideCompletedQuery) ->
        taskDao.getTasks(searchQuery, sortOrderQuery, hideCompletedQuery)
    }

    val tasks = tasksFlow.asLiveData()
}

enum class SortOrder{BY_DATE, BY_NAME}