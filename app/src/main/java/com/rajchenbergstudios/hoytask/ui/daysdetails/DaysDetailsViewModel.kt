package com.rajchenbergstudios.hoytask.ui.daysdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rajchenbergstudios.hoytask.data.day.Day
import com.rajchenbergstudios.hoytask.data.day.DayDao
import com.rajchenbergstudios.hoytask.util.Converters
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DaysDetailsViewModel @Inject constructor(
    private val dayDao: DayDao,
    stateHandle: SavedStateHandle
) : ViewModel(){

    val day = stateHandle.get<Day>("day")

    var dayWeekDay = stateHandle["weekDay"] ?: day?.dayOfWeek ?: "null"

    var dayMonth = stateHandle["month"] ?: day?.month ?: "null"

    var dayYear = stateHandle["year"] ?: day?.year ?: "null"

    var dayMonthDay = stateHandle["monthDay"] ?: day?.dayOfMonth ?: "null"

//    var tasks = stateHandle["tasksList"] ?:
//    Converters.fromJsonToTaskList(stateHandle["tasksList"] ?: day?.tasksListJson ?: "null") ?: listOf()
}