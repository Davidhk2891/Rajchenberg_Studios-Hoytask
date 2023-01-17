package com.rajchenbergstudios.hoygenda.ui.more.leavereview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaveReviewViewModel @Inject constructor(

) : ViewModel(){

    // Leave a review Channel
    private val leaveReviewEventChannel = Channel<LeaveReviewEvent>()

    // Leave a review Event
    val leaveReviewEvent = leaveReviewEventChannel.receiveAsFlow()

    fun onRateNowClick() = viewModelScope.launch {
        leaveReviewEventChannel.send(LeaveReviewEvent.NavigateToRateOnGooglePlay)
    }

    fun onMaybeLaterClick() = viewModelScope.launch {
        leaveReviewEventChannel.send(LeaveReviewEvent.DoMaybeLaterAction)
    }

    fun onDoNotAskMeAgainClick() = viewModelScope.launch {
        leaveReviewEventChannel.send(LeaveReviewEvent.DoNotAskMeAgainAction)
    }

    sealed class LeaveReviewEvent {
        object NavigateToRateOnGooglePlay : LeaveReviewEvent()
        object DoMaybeLaterAction : LeaveReviewEvent()
        object DoNotAskMeAgainAction : LeaveReviewEvent()
    }
}