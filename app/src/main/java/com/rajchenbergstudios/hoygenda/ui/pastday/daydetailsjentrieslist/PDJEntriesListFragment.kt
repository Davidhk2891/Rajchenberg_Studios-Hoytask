package com.rajchenbergstudios.hoygenda.ui.pastday.daydetailsjentrieslist

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.databinding.FragmentChildPdJournalEntriesListBinding
import com.rajchenbergstudios.hoygenda.ui.pastday.SharedDayDetailsViewModel
import com.rajchenbergstudios.hoygenda.ui.pastday.daydetailstaskslist.TAG
import com.rajchenbergstudios.hoygenda.utils.HGDAViewStateUtils
import com.rajchenbergstudios.hoygenda.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PDJEntriesListFragment : Fragment(R.layout.fragment_child_pd_journal_entries_list) {

    private val sharedViewModel: SharedDayDetailsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentChildPdJournalEntriesListBinding.bind(view)

        val pdJEntriesListAdapter = PDJEntriesListAdapter()

        binding.apply {

            journalEntriesListRecyclerview.layoutTasksListRecyclerview.apply {
                adapter = pdJEntriesListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        loadMenu()
        loadObservable(sharedViewModel, binding, pdJEntriesListAdapter)
    }

    private fun loadObservable(viewModel: SharedDayDetailsViewModel, binding: FragmentChildPdJournalEntriesListBinding, pdjEntriesListAdapter: PDJEntriesListAdapter) {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            withContext(Dispatchers.IO) {
                binding.apply {
                    HGDAViewStateUtils.apply {
                        Logger.i(TAG, "loadObservable", "some jEntries data: ${viewModel.dayMonth} - ${viewModel.dayYear}: ${viewModel.jEntriesList}")
                        if (viewModel.jEntriesList?.isEmpty() == true) {
                            setViewVisibility(journalEntriesListRecyclerview.layoutTasksListRecyclerview, visibility = View.INVISIBLE)
                            setViewVisibility(journalEntriesListLayoutNoData.layoutNoDataLinearlayout, visibility = View.VISIBLE)
                        } else {
                            setViewVisibility(journalEntriesListRecyclerview.layoutTasksListRecyclerview, visibility = View.VISIBLE)
                            setViewVisibility(journalEntriesListLayoutNoData.layoutNoDataLinearlayout, visibility = View.INVISIBLE)
                            pdjEntriesListAdapter.submitList(viewModel.jEntriesList)
                        }
                    }
                }
            }
        }
    }

    private fun loadMenu(){
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(JEntriesMenuProvider(), viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private inner class JEntriesMenuProvider : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menu.clear()
            // menuInflater.inflate(R.menu.menu_jentries_list_fragment, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                else -> false
            }
        }
    }
}