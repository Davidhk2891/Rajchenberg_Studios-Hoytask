package com.rajchenbergstudios.hoygenda.ui.more.leavereview

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoygenda.data.constants.ReviewsAndSharingConstants
import com.rajchenbergstudios.hoygenda.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaveReviewDialogViewModel @Inject constructor(

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

    fun rateApp(activity: FragmentActivity?){
        try {
            activity?.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(ReviewsAndSharingConstants.APP_MARKET_URL_GOOGLE_PLAY)
                )
            )
        } catch (e : ActivityNotFoundException) {
            activity?.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(ReviewsAndSharingConstants.APP_URL_GOOGLE_PLAY)
                )
            )
        }
    }

    sealed class LeaveReviewEvent {
        object NavigateToRateOnGooglePlay : LeaveReviewEvent()
        object DoMaybeLaterAction : LeaveReviewEvent()
        object DoNotAskMeAgainAction : LeaveReviewEvent()
    }
}