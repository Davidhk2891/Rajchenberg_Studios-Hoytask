package com.rajchenbergstudios.hoytask.ui.dayshistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoytask.data.day.Day
import com.rajchenbergstudios.hoytask.data.day.DayDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DaysListViewModel @Inject constructor(
    private val dayDao: DayDao
) : ViewModel(){

    // Days channel
    private val daysEventChannel = Channel<DaysEvent>()

    // Days event
    val daysEvent = daysEventChannel.receiveAsFlow()

    val days = dayDao.getDays().asLiveData()

    fun onDaySelected(day: Day) = viewModelScope.launch {
        daysEventChannel.send(DaysEvent.NavigateToDaysDetailsScreen(day))
    }

    sealed class DaysEvent {
        data class NavigateToDaysDetailsScreen(val day: Day) : DaysEvent()
    }
}