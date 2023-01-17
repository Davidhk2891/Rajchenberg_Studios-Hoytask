package com.rajchenbergstudios.hoygenda.ui.more.leavereview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.databinding.FragmentLeaveAReviewBinding
import dagger.hilt.android.AndroidEntryPoint

// private const val TAG = "LeaveReviewDialogFragment"

@AndroidEntryPoint
class LeaveReviewDialogFragment : DialogFragment(){

    private val viewModel : LeaveReviewDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_leave_a_review, container, false)

        val binding = FragmentLeaveAReviewBinding.bind(view)

        dialog?.setCanceledOnTouchOutside(false)

        binding.apply {
            leaveAReviewOptionsRateNowButton.setOnClickListener {
                viewModel.onRateNowClick()
            }
            leaveAReviewOptionsMaybeLaterButton.setOnClickListener {
                viewModel.onMaybeLaterClick()
            }
            leaveAReviewOptionsNoAskAgainButton.setOnClickListener {
                viewModel.onDoNotAskMeAgainClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.leaveReviewEvent.collect{ event ->
                when (event) {
                    is LeaveReviewDialogViewModel.LeaveReviewEvent.DoMaybeLaterAction -> {
                        dismiss()
                    }
                    is LeaveReviewDialogViewModel.LeaveReviewEvent.DoNotAskMeAgainAction -> {
                        dismiss()
                    }
                    is LeaveReviewDialogViewModel.LeaveReviewEvent.NavigateToRateOnGooglePlay -> {
                        viewModel.rateApp(activity)
                    }
                }
            }
        }

        return view
    }
}