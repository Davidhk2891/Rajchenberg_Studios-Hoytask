package com.rajchenbergstudios.hoygenda.ui.pastday

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rajchenbergstudios.hoygenda.data.day.Day
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedDayDetailsViewModel @Inject constructor(
    stateHandle: SavedStateHandle
) : ViewModel(){

    val day = stateHandle.get<Day>("day")

    val dayWeekDay = stateHandle["weekDay"] ?: day?.dayOfWeek ?: "null"

    val dayMonth = stateHandle["month"] ?: day?.month ?: "null"

    val dayYear = stateHandle["year"] ?: day?.year ?: "null"

    val dayMonthDay = stateHandle["monthDay"] ?: day?.dayOfMonth ?: "null"

    val tasksList = day?.listOfTasks

    val jEntriesList = day?.listOfJEntries

}