package com.rajchenbergstudios.hoygenda.ui.deleteall

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rajchenbergstudios.hoygenda.R
import dagger.hilt.android.AndroidEntryPoint

// private const val TAG = "DeleteAllDialogFragment.kt"

@AndroidEntryPoint
class DeleteAllDialogFragment : DialogFragment(){

    private val viewModel: DeleteAllDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialog: AlertDialog
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setTitle("Confirm deletion")
            setMessage(viewModel.populateMessage())
            setNegativeButton("cancel", null)
            setPositiveButton("Yes") { _, _ ->
                viewModel.onConfirmClick(object: DeleteAllDialogViewModel.ResultInterface{
                    override fun onShowEmptyListMessage(message: String) {
                        viewModel.showNothingToDeleteMessage(message)
                    }
                })
            }
        }

        alertDialog = builder.create()
        alertDialog.show()
        val pButton: Button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        val nButton: Button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        pButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.hoytask_purple))
        nButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.hoytask_purple))
        return alertDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.lifecycleScope.launchWhenStarted {
            viewModel.deleteAllEvent.collect{ event ->
                when (event) {
                    is DeleteAllDialogViewModel.DeleteAllEvent.ShowNothingToDeleteMessage -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}