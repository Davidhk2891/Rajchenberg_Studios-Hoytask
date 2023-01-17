package com.rajchenbergstudios.hoygenda.ui.more.tellyourfriends

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoygenda.data.constants.ReviewsAndSharingConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TellYourFriendsDialogViewModel @Inject constructor(

) : ViewModel() {

    // Tell your friends event channel
    private val tellYourFriendsEventChannel = Channel<TellYourFriendsEvent>()

    // Tell your friends event
    val tellYourFriendsEvent = tellYourFriendsEventChannel.receiveAsFlow()

    fun onShareButtonClick() = viewModelScope.launch {
        tellYourFriendsEventChannel.send(TellYourFriendsEvent.ShareAppLinkWithChooser)
    }

    fun shareAppLink(activity: FragmentActivity?) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
        val shareMessage = "Hey! I am using Hoygenda to keep track of my daily tasks and my journal. Check it out!\n\n"
        val shareLink = ReviewsAndSharingConstants.APP_URL_GOOGLE_PLAY
        val shareContent = shareMessage + shareLink
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent.trimIndent())
        activity?.startActivity(Intent.createChooser(shareIntent, "Choose an app to share Hoygenda"))
    }

    sealed class TellYourFriendsEvent {
        object ShareAppLinkWithChooser : TellYourFriendsEvent()
    }
}