package com.rajchenbergstudios.hoygenda.ui.tutorial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rajchenbergstudios.hoygenda.data.prefs.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TutorialViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    // Channel
    private val tutorialEventChannel = Channel<TutorialEvent>()

    // Event
    val tutorialEvent = tutorialEventChannel.receiveAsFlow()

    fun onSaveTutorialAutoRunDone() = viewModelScope.launch {
        preferencesManager.setTutorialAutoRunSettingKey()
    }

    fun onNavigateToTodayFragmentEngaged() = viewModelScope.launch {
        tutorialEventChannel.send(TutorialEvent.NavigateToTodayFragment)
    }

    sealed class TutorialEvent {
        object NavigateToTodayFragment : TutorialEvent()
    }
}