package com.rajchenbergstudios.hoytask.ui.dayshistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoytask.data.day.DayDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DaysListViewModel @Inject constructor(
    private val dayDao: DayDao
) : ViewModel(){

    val days = viewModelScope.launch {
        dayDao.getDays()
    }
}