package com.rajchenbergstudios.hoygenda.ui.jentryaddedit

import androidx.lifecycle.ViewModel
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntryDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class JEntryAddEditViewModel @Inject constructor(
    private val journalEntryDao: JournalEntryDao
) : ViewModel(){


}