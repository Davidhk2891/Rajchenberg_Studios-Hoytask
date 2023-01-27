package com.rajchenbergstudios.hoygenda.ui.core.taskaddedit

import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.databinding.FragmentAddEditTaskBinding
import com.rajchenbergstudios.hoygenda.utils.focusAndShowKeyboardInFragment
import com.rajchenbergstudios.hoygenda.utils.HGDAViewStateUtils
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

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditEvent.collect { event ->
                when (event) {
                    is TaskAddEditViewModel.AddEditEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is TaskAddEditViewModel.AddEditEvent.NavigateBackWithResult -> {
                        binding.fragmentAddEditTitleEdittext.clearFocus()
                        when (viewModel.origin) {
                            1 -> {
                                setFragmentResult(
                                    "task_add_edit_request",
                                    bundleOf("task_add_edit_result" to event.result)
                                )
                            }
                            2 -> {
                                setFragmentResult(
                                    "task_in_set_add_edit_request",
                                    bundleOf("task_in_set_add_edit_result" to event.result)
                                )
                            }
                        }
                        findNavController().popBackStack()
                    }
                    is TaskAddEditViewModel.AddEditEvent.ShowFlowFromTaskInSetList -> {
                        showFlowFromTaskInSetList(binding)
                    }
                    is TaskAddEditViewModel.AddEditEvent.ShowFlowFromTaskList -> {
                        showFlowFromTaskList(binding)
                    }
                    TaskAddEditViewModel.AddEditEvent.ShowFlowFromPastDayTaskList -> {
                        showFlowFromPastDayTaskList(binding)
                    }
                }.exhaustive
            }
        }

        focusEditText(binding.fragmentAddEditTitleEdittext)
        loadMenu()
        deduceUserFlow()
    }

    private fun focusEditText(editText: EditText) {
        if (viewModel.origin == 1)
            editText.focusAndShowKeyboardInFragment(requireActivity())
    }

    private fun deduceUserFlow() {
        viewModel.deduceFlow()
    }

    private fun loadMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(AddEditTaskMenuProvide(), viewLifecycleOwner, Lifecycle.State.CREATED)
    }

    private inner class AddEditTaskMenuProvide: MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menu.clear()
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
//                android.R.id.home -> findNavController().popBackStack()
                else -> false
            }
        }
    }

    private fun showFlowFromTaskList(binding: FragmentAddEditTaskBinding) {
        binding.apply {
            fragmentAddEditTitleEdittext.setText(viewModel.taskName)
            fragmentAddEditImportantCheckbox.isChecked = viewModel.taskImportance
            fragmentAddEditImportantCheckbox.jumpDrawablesToCurrentState()
            fragmentAddEditCreatedTextview.isVisible = viewModel.mTask != null
            val dateCreated = "Created at: ${viewModel.mTask?.createdTimeFormat}"
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
            fragmentAddEditImportantCheckbox.isVisible = viewModel.mTask != null
            fragmentAddEditImportantCheckbox.isClickable = false
            fragmentAddEditCreatedTextview.isVisible = viewModel.mTask != null

            fragmentAddEditTitleEdittext.addTextChangedListener { newText ->
                viewModel.taskInSetName = newText.toString()
            }
        }
    }

    private fun showFlowFromPastDayTaskList(binding: FragmentAddEditTaskBinding) {
        binding.apply {
            fragmentAddEditTitleEdittext.setText(viewModel.taskName)
            fragmentAddEditImportantCheckbox.isChecked = viewModel.taskImportance
            fragmentAddEditImportantCheckbox.jumpDrawablesToCurrentState()

            HGDAViewStateUtils.setViewClickState(v1 = fragmentAddEditTitleEdittext, v2 = fragmentAddEditImportantCheckbox, v3 = fragmentAddEditFab, clickable = false)
            HGDAViewStateUtils.setViewVisibility(v1 = fragmentAddEditCreatedTextview, v2 = fragmentAddEditFab, visibility = View.INVISIBLE)
            HGDAViewStateUtils.setViewFocusability(v1 = fragmentAddEditTitleEdittext, focusable = false)
        }
    }
}