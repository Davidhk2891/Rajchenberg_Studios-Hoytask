package com.rajchenbergstudios.hoygenda.ui.more.tellyourfriends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.databinding.FragmentTellYourFriendsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TellYourFriendsDialogFragment : DialogFragment(){

    private val viewModel: TellYourFriendsDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_tell_your_friends, container, false)

        val binding = FragmentTellYourFriendsBinding.bind(view)

        binding.apply {
            tellYourFriendsOptionsShareButton.setOnClickListener {
                viewModel.onShareButtonClick()
            }
            tellYourFriendsOptionsCancelButton.setOnClickListener {
                dismiss()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tellYourFriendsEvent.collect { event ->
                when (event) {
                   is TellYourFriendsDialogViewModel.TellYourFriendsEvent.ShareAppLinkWithChooser -> {
                       viewModel.shareAppLink(activity)
                   }
                }
            }
        }

        return view
    }
}