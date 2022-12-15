package com.rajchenbergstudios.hoygenda.ui.jentryaddedit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rajchenbergstudios.hoygenda.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JEntryAddEditFragment : Fragment(R.layout.fragment_child_journal_entries_list) {

    private val viewModel : JEntryAddEditViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}