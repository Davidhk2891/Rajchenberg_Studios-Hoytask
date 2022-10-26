package com.rajchenbergstudios.hoytask.ui.deleteall

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.rajchenbergstudios.hoytask.R
import dagger.hilt.android.AndroidEntryPoint

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
                viewModel.onConfirmClick()
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
}