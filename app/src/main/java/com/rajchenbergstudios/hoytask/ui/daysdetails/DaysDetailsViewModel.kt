package com.rajchenbergstudios.hoytask.ui.daysdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rajchenbergstudios.hoytask.data.day.Day
import com.rajchenbergstudios.hoytask.data.task.Task
import com.rajchenbergstudios.hoytask.util.Converters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DaysDetailsViewModel @Inject constructor(
    stateHandle: SavedStateHandle
) : ViewModel(){

    val day = stateHandle.get<Day>("day")

    val dayWeekDay = stateHandle["weekDay"] ?: day?.dayOfWeek ?: "null"

    val dayMonth = stateHandle["month"] ?: day?.month ?: "null"

    val dayYear = stateHandle["year"] ?: day?.year ?: "null"

    val dayMonthDay = stateHandle["monthDay"] ?: day?.dayOfMonth ?: "null"

    var tasks: List<Task> = stateHandle["tasksList"] ?: Converters.fromJsonToTaskList(
        stateHandle["tasksList"] ?: day?.tasksListJson ?: "null"
    ) ?: listOf()
}