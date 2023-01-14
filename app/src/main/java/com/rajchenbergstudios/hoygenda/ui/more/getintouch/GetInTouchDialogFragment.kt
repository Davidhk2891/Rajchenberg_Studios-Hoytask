package com.rajchenbergstudios.hoygenda.ui.more.getintouch

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.databinding.FragmentGetInTouchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GetInTouchDialogFragment : DialogFragment(){

    private val viewModel: GetInTouchDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View = inflater.inflate(R.layout.fragment_get_in_touch, container, false)

        val binding = FragmentGetInTouchBinding.bind(view)

        binding.apply {
            getInTouchOptionsEmailUsButton.setOnClickListener{
                viewModel.onEmailUsClick()
            }
            getInTouchOptionsCancelButton.setOnClickListener {
                viewModel.onCancelClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getInTouchEvent.collect{ event->
                when(event) {
                    is GetInTouchDialogViewModel.GetInTouchEvent.DoGetInTouchCancellationAction -> {
                        dismiss()
                    }
                    is GetInTouchDialogViewModel.GetInTouchEvent.GoToEmailClient -> {
                        runEmailClient(event.recipient, event.subject, event.content)
                    }
                }
            }
        }

        return view
    }

    private fun runEmailClient(recipient: String, subject: String, content: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        intent.putExtra(Intent.EXTRA_SUBJECT,subject)
        intent.putExtra(Intent.EXTRA_TEXT, content)
        intent.type = "message/rfc822"
        startActivity(Intent.createChooser(intent, "Choose an Email client :"))
    }
}