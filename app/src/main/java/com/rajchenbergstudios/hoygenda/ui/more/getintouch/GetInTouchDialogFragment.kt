package com.rajchenbergstudios.hoygenda.ui.more.getintouch

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.databinding.FragmentGetInTouchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GetInTouchDialogFragment : DialogFragment(){

    private val viewModel: GetInTouchDialogViewModel by viewModels()

    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_get_in_touch, container, false)
        initEmailLauncher()
        initViewAndViewEvents(FragmentGetInTouchBinding.bind(view))
        loadEventCollector()
        return view
    }

    private fun initViewAndViewEvents(binding: FragmentGetInTouchBinding) {
        binding.apply {
            getInTouchOptionsEmailUsButton.setOnClickListener {
                viewModel.onEmailUsClick()
            }
            getInTouchOptionsCancelButton.setOnClickListener {
                viewModel.onCancelClick()
            }
        }
    }

    private fun loadEventCollector() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getInTouchEvent.collect{ event->
                when(event) {
                    is GetInTouchDialogViewModel.GetInTouchEvent.DoGetInTouchCancellationAction -> {
                        dismiss()
                    }
                    is GetInTouchDialogViewModel.GetInTouchEvent.DismissDialog -> {
                        dismiss()
                    }
                    is GetInTouchDialogViewModel.GetInTouchEvent.GoToEmailClient -> {
                        composeAndLaunchEmail(event.recipient, event.subject, event.content)
                    }
                }
            }
        }
    }

    /*
     Email operations are to be handled from the Fragment since it needs easy access
     to intent, context, activity.
     */
    private fun composeAndLaunchEmail(recipient: String, subject: String, content: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            // Uri.parse("mailto:") means only email apps should handle this
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT,subject)
            putExtra(Intent.EXTRA_TEXT, content)
        }
        try {
            launcher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "There are no applications that " +
                    "support this action", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initEmailLauncher() {
        launcher = this.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            // There is an issue when getting confirmation data from Gmail or any client
            viewModel.dismissDialog()
        }
    }
}