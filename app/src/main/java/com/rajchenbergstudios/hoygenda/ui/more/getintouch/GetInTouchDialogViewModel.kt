package com.rajchenbergstudios.hoygenda.ui.more.getintouch

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoygenda.data.constants.GetInTouchConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GetInTouchDialogViewModel @Inject constructor(

) : ViewModel(){

    // getInTouch Channel
    private  val getInTouchEventChannel = Channel<GetInTouchEvent>()

    // getInTouch Event
    val getInTouchEvent = getInTouchEventChannel.receiveAsFlow()

    fun onEmailUsClick() = viewModelScope.launch {

        val recipient = GetInTouchConstants.SUPPORT_EMAIL_RECIPIENT
        val subject = GetInTouchConstants.SUPPORT_EMAIL_SUBJECT
        val content = GetInTouchConstants.SUPPORT_EMAIL_CONTENT

        getInTouchEventChannel.send(GetInTouchEvent.GoToEmailClient(
            recipient, subject, content
        ))
    }

    fun onCancelClick() = viewModelScope.launch {
        getInTouchEventChannel.send(GetInTouchEvent.DoGetInTouchCancellationAction)
    }

    fun runEmailClient(activity: FragmentActivity?, recipient: String, subject: String, content: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        intent.putExtra(Intent.EXTRA_SUBJECT,subject)
        intent.putExtra(Intent.EXTRA_TEXT, content)
        intent.type = "message/rfc822"
        activity?.startActivity(Intent.createChooser(intent, "Choose an Email client :"))
    }

    sealed class GetInTouchEvent {
        object DoGetInTouchCancellationAction : GetInTouchEvent()
        data class GoToEmailClient(val recipient: String, val subject: String, val content: String) : GetInTouchEvent()
    }
}