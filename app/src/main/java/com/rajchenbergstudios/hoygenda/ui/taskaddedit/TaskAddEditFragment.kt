package com.rajchenbergstudios.hoygenda.ui.taskaddedit

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.databinding.FragmentAddEditTaskBinding
import com.rajchenbergstudios.hoygenda.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint

// private const val TAG = "TaskAddEditFragment.kt"

@AndroidEntryPoint
class TaskAddEditFragment : Fragment(R.layout.fragment_add_edit_task){

    private val viewModel: TaskAddEditViewModel by viewModels()

    @Suppress("IMPLICIT_CAST_TO_ANY")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditTaskBinding.bind(view)

        binding.apply {
            fragmentAddEditFab.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        deduceUserFlow()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditEvent.collect { event ->
                when (event) {
                    is TaskAddEditViewModel.AddEditEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is TaskAddEditViewModel.AddEditEvent.NavigateBackWithResult -> {
                        binding.fragmentAddEditTitleEdittext.clearFocus()
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is TaskAddEditViewModel.AddEditEvent.ShowFlowFromTaskInSetList -> {
                        showFlowFromTaskInSetList(binding)
                    }
                    is TaskAddEditViewModel.AddEditEvent.ShowFlowFromTaskList -> {
                        showFlowFromTaskList(binding)
                    }
                }.exhaustive
            }
        }
    }

    private fun deduceUserFlow() {
        viewModel.deduceFlow()
    }

    private fun showFlowFromTaskList(binding: FragmentAddEditTaskBinding) {
        binding.apply {
            fragmentAddEditTitleEdittext.setText(viewModel.taskName)
            fragmentAddEditImportantCheckbox.isChecked = viewModel.taskImportance
            fragmentAddEditImportantCheckbox.jumpDrawablesToCurrentState()
            fragmentAddEditCreatedTextview.isVisible = viewModel.task != null
            val dateCreated = "Date created: ${viewModel.task?.createdDateFormat}"
            fragmentAddEditCreatedTextview.text = dateCreated

            fragmentAddEditTitleEdittext.addTextChangedListener { newText ->
                viewModel.taskName = newText.toString()
            }

            fragmentAddEditImportantCheckbox.setOnCheckedChangeListener { _, isChecked ->
                viewModel.taskImportance = isChecked
            }
        }
    }

    private fun showFlowFromTaskInSetList(binding: FragmentAddEditTaskBinding) {
        binding.apply {
            fragmentAddEditTitleEdittext.setText(viewModel.taskInSetName)
            fragmentAddEditImportantCheckbox.isVisible = viewModel.task != null
            fragmentAddEditImportantCheckbox.isClickable = false
            fragmentAddEditCreatedTextview.isVisible = viewModel.task != null

            fragmentAddEditTitleEdittext.addTextChangedListener { newText ->
                viewModel.taskInSetName = newText.toString()
            }
        }
    }
}