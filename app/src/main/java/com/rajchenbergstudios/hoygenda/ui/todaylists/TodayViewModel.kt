package com.rajchenbergstudios.hoygenda.ui.todaylists

import androidx.lifecycle.ViewModel
import com.rajchenbergstudios.hoygenda.utils.HGDADateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class TodayViewModel @Inject constructor(

) : ViewModel() {

    fun getCurrentDayOfMonth(): String {
        return HGDADateUtils.currentDayOfMonthFormatted
    }

    fun getCurrentMonth(): String {
        return HGDADateUtils.currentMonthFormatted
    }

    fun getCurrentYear(): String {
        return HGDADateUtils.currentYearFormatted
    }

    fun getCurrentDayOfWeek(): String {
        return HGDADateUtils.currentDayOfWeekFormatted
    }
}