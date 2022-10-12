package com.rajchenbergstudios.hoytask.ui.createtaskset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rajchenbergstudios.hoytask.R
import com.rajchenbergstudios.hoytask.databinding.FragmentTaskSetCreateBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateTaskSetDialogFragment : DialogFragment(){

    private val viewModel: CreateTaskSetDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View = inflater.inflate(R.layout.fragment_task_set_create, container, false)

        val binding = FragmentTaskSetCreateBinding.bind(view)

        binding.apply {
            taskSetCreateOptionsCreateButton.setOnClickListener {
                viewModel.onCreateTaskSetClick(taskSetCreateSetTitleEdittext.text.toString())
            }
            taskSetCreateOptionsCancelButton.setOnClickListener {
                viewModel.onCancelTaskSetClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.createTaskSetEvent.collect { event ->
                when (event) {
                    is CreateTaskSetDialogViewModel.CreateTaskSetEvent.ShowInvalidInputMessage -> {
                        Toast.makeText(requireContext(), event.msg, Toast.LENGTH_LONG).show()
                    }
                    is CreateTaskSetDialogViewModel.CreateTaskSetEvent.DoTaskSetCancellationAction -> {
                        dismiss()
                    }
                    is CreateTaskSetDialogViewModel.CreateTaskSetEvent.GoToSetAndShowSetCreatedConfirmationMessage -> {
                        Toast.makeText(requireContext(), event.msg, Toast.LENGTH_LONG).show()
                        val action = CreateTaskSetDialogFragmentDirections
                            .actionCreateTaskSetDialogFragmentToTasksSetEditListFragment(settitle = event.title)
                        findNavController().navigate(action)
                    }
                }
            }
        }

        return view
    }
}