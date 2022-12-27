package com.rajchenbergstudios.hoygenda.ui.jentryaddedit

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
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
import com.rajchenbergstudios.hoygenda.databinding.FragmentAddEditJournalEntryBinding
import com.rajchenbergstudios.hoygenda.utils.HGDAViewStateUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JEntryAddEditFragment : Fragment(R.layout.fragment_add_edit_journal_entry) {

    private val viewModel : JEntryAddEditViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditJournalEntryBinding.bind(view)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.jeAddEditEvent.collect { event ->
                when (event) {
                    is JEntryAddEditViewModel.JEAddEditEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }
                    is JEntryAddEditViewModel.JEAddEditEvent.NavigateBackWithResult -> {
                        binding.fragmentJentryAddEditTitleEdittext.clearFocus()
                        setFragmentResult(
                            "je_add_edit_request",
                            bundleOf("je_add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    JEntryAddEditViewModel.JEAddEditEvent.ShowFlowFromJEntriesList -> {
                        showFlowFromJEntriesList(binding)
                    }
                    JEntryAddEditViewModel.JEAddEditEvent.ShowFlowFromPastDayJEntriesList -> {
                        showFlowFromPastJEntriesList(binding)
                    }
                }
            }
        }

        loadMenu()
        deduceUserFlow()
    }

    private fun deduceUserFlow() {
        viewModel.deduceFlow()
    }

    private fun showFlowFromJEntriesList(binding: FragmentAddEditJournalEntryBinding) {
        binding.apply {
            fragmentJentryAddEditTitleEdittext.setText(viewModel.jentryText)
            fragmentJentryAddEditImportantCheckbox.isChecked = viewModel.jentryImportance
            fragmentJentryAddEditImportantCheckbox.jumpDrawablesToCurrentState()
            fragmentJentryAddEditCreatedTextview.isVisible = viewModel.jentry != null
            val timeCreated = "created at: ${viewModel.jentry?.createdTimeFormat}"
            fragmentJentryAddEditCreatedTextview.text = timeCreated

            fragmentJentryAddEditTitleEdittext.addTextChangedListener { newText ->
                viewModel.jentryText = newText.toString()
            }

            fragmentJentryAddEditImportantCheckbox.setOnCheckedChangeListener { _, isChecked ->
                viewModel.jentryImportance = isChecked
            }

            fragmentJentryAddEditFab.setOnClickListener {
                viewModel.onSaveClick()
            }
        }
    }

    private fun showFlowFromPastJEntriesList(binding: FragmentAddEditJournalEntryBinding) {
        binding.apply {
            fragmentJentryAddEditTitleEdittext.setText(viewModel.jentryText)
            fragmentJentryAddEditImportantCheckbox.isChecked = viewModel.jentryImportance
            fragmentJentryAddEditImportantCheckbox.jumpDrawablesToCurrentState()

            HGDAViewStateUtils.setViewClickState(v1 = fragmentJentryAddEditTitleEdittext, v2 = fragmentJentryAddEditImportantCheckbox, v3 = fragmentJentryAddEditFab, clickable = false)
            HGDAViewStateUtils.setViewVisibility(v1 = fragmentJentryAddEditCreatedTextview, v2 = fragmentJentryAddEditFab, visibility = View.INVISIBLE)
            HGDAViewStateUtils.setViewFocusability(v1 = fragmentJentryAddEditTitleEdittext, focusable = false)
        }
    }

    private fun loadMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(AddEditJEntryMenuProvide(), viewLifecycleOwner, Lifecycle.State.CREATED)
    }

    private inner class AddEditJEntryMenuProvide: MenuProvider {
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
}