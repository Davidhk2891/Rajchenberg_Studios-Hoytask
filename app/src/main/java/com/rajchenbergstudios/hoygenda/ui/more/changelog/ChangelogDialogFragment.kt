package com.rajchenbergstudios.hoygenda.ui.more.changelog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.data.constants.ChangelogConstants
import com.rajchenbergstudios.hoygenda.databinding.FragmentChangelogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangelogDialogFragment : DialogFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_changelog, container, false)

        val binding = FragmentChangelogBinding.bind(view)

        binding.apply {
            changelogVersionTextview.text = ChangelogConstants.LATEST_VERSION_NAME
            changelogContentTextview.text = ChangelogConstants.LATEST_VERSION_CHANGES

            changelogOptionsOkButton.setOnClickListener {
                dismiss()
            }
        }
        return view
    }
}