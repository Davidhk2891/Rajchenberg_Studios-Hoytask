package com.rajchenbergstudios.hoytask.ui.tasksdeleteallcompleted

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksDeleteAllCompletedDialogFragment : DialogFragment(){

    private val viewModel: TasksDeleteAllCompletedDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
        = AlertDialog.Builder(requireContext())
        .setTitle("Confirm deletion")
        .setMessage("Do you really want to delete all completed tasks?")
        .setNegativeButton("cancel", null)
        .setPositiveButton("Yes"){_, _ ->
            viewModel.onConfirmClick()
        }
        .create()
}