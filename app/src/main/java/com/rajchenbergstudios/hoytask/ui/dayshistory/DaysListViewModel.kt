package com.rajchenbergstudios.hoytask.ui.dayshistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rajchenbergstudios.hoytask.data.day.DayDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DaysListViewModel @Inject constructor(
    private val dayDao: DayDao
) : ViewModel(){

    val days = dayDao.getDays().asLiveData()
}