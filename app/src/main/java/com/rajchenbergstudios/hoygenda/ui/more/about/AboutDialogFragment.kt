package com.rajchenbergstudios.hoygenda.ui.more.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.data.constants.GetInTouchAndAboutConstants
import com.rajchenbergstudios.hoygenda.databinding.FragmentAboutBinding

class AboutDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_about, container, false)

        val binding = FragmentAboutBinding.bind(view)

        binding.apply {
            val aboutText = "Build name: ${GetInTouchAndAboutConstants.versionName}\n" +
                            "Build number: ${GetInTouchAndAboutConstants.versionCode}"
            aboutContentTextview.text = aboutText
            aboutOptionsThanksButton.setOnClickListener {
                dismiss()
            }
        }

        return view
    }
}