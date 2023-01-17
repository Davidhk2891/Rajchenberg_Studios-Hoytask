package com.rajchenbergstudios.hoygenda.ui.more.leavereview

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.data.constants.LeaveReviewConstants
import com.rajchenbergstudios.hoygenda.databinding.FragmentLeaveAReviewBinding
import com.rajchenbergstudios.hoygenda.utils.Logger
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "LeaveReviewDialogFragment"

@AndroidEntryPoint
class LeaveReviewDialogFragment : DialogFragment(){

    private val viewModel : LeaveReviewViewModel by viewModels()

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
                    is LeaveReviewViewModel.LeaveReviewEvent.DoMaybeLaterAction -> {
                        dismiss()
                    }
                    is LeaveReviewViewModel.LeaveReviewEvent.DoNotAskMeAgainAction -> {
                        dismiss()
                    }
                    is LeaveReviewViewModel.LeaveReviewEvent.NavigateToRateOnGooglePlay -> {
                        rateApp()
                    }
                }
            }
        }

        return view
    }

    private fun rateApp(){
        try {
            startActivity(Intent(
                Intent.ACTION_VIEW,
                Uri.parse(LeaveReviewConstants.APP_MARKET_URL_GOOGLE_PLAY)
            ))
            Logger.i(TAG, "setUpNavigationViewNavigation", "Market called")
        } catch (e : ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(LeaveReviewConstants.APP_URL_GOOGLE_PLAY)
                )
            )
            Logger.i(TAG, "setUpNavigationViewNavigation", "Uri called")
        }
    }

}