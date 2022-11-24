package com.rajchenbergstudios.hoygenda.ui.todaylists.journalentrieslist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.rajchenbergstudios.hoygenda.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class JEntriesListFragment : Fragment(R.layout.fragment_child_journal_entries_list) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}