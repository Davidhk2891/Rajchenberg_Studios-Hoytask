package com.rajchenbergstudios.hoygenda.ui.core.pastday.daydetailsjentrieslist

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.data.prefs.SortOrder
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntry
import com.rajchenbergstudios.hoygenda.databinding.FragmentChildPdJournalEntriesListBinding
import com.rajchenbergstudios.hoygenda.ui.core.pastday.DaysDetailsFragmentDirections
import com.rajchenbergstudios.hoygenda.ui.core.pastday.DayDetailsViewModel
import com.rajchenbergstudios.hoygenda.ui.core.pastday.daydetailstaskslist.TAG
import com.rajchenbergstudios.hoygenda.utils.HGDAViewStateUtils
import com.rajchenbergstudios.hoygenda.utils.Logger
import com.rajchenbergstudios.hoygenda.utils.exhaustive
import com.rajchenbergstudios.hoygenda.utils.onQueryTextChanged
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class PDJEntriesListFragment : Fragment(R.layout.fragment_child_pd_journal_entries_list),
    PDJEntriesListAdapter.OnItemClickListener {

    private val viewModel: DayDetailsViewModel by viewModels()
    private lateinit var searchView: SearchView

    private lateinit var menuHost: MenuHost

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentChildPdJournalEntriesListBinding.bind(view)

        val pdJEntriesListAdapter = PDJEntriesListAdapter(this)

        binding.apply {

            journalEntriesListRecyclerview.layoutTasksListRecyclerview.apply {
                adapter = pdJEntriesListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        loadMenu()
        loadObservable(viewModel, binding, pdJEntriesListAdapter)
        loadPastDayJEntryEventCollector()
    }

    private fun loadObservable(viewModel: DayDetailsViewModel, binding: FragmentChildPdJournalEntriesListBinding, pdjEntriesListAdapter: PDJEntriesListAdapter) {
        this.viewModel.jEntries?.observe(viewLifecycleOwner){ jEntriesList ->
            binding.apply {
                HGDAViewStateUtils.apply {
                    Logger.i(TAG, "loadObservable", "some jEntries data: ${viewModel.dayMonth} - ${viewModel.dayYear}: ${viewModel.jEntriesList}")
                    if (jEntriesList.isEmpty()) {
                        setViewVisibility(journalEntriesListRecyclerview.layoutTasksListRecyclerview, visibility = View.INVISIBLE)
                        setViewVisibility(journalEntriesListLayoutNoData.layoutNoDataLinearlayout, visibility = View.VISIBLE)
                    } else {
                        setViewVisibility(journalEntriesListRecyclerview.layoutTasksListRecyclerview, visibility = View.VISIBLE)
                        setViewVisibility(journalEntriesListLayoutNoData.layoutNoDataLinearlayout, visibility = View.INVISIBLE)
                        pdjEntriesListAdapter.submitList(jEntriesList)
                    }
                }
            }
        }
    }

    private fun loadPastDayJEntryEventCollector() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.pastDayJEntryEvent.collect { pastDayJEntryEvent ->
                when (pastDayJEntryEvent) {
                    is DayDetailsViewModel.PastDayJEntryEvent.NavigateToJEntryDetailsScreen -> {
                        val action = DaysDetailsFragmentDirections.actionDaysDetailsFragmentToJEntryAddEditFragment(title = "Entry from ${pastDayJEntryEvent.date}"
                            , jentry = pastDayJEntryEvent.journalEntry, origin = 2)
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }
    }

    private fun loadMenu(){
        menuHost = requireActivity()
        menuHost.addMenuProvider(JEntriesMenuProvider(), viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private inner class JEntriesMenuProvider : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menu.clear()
            menuInflater.inflate(R.menu.menu_pd_jentries_list_fragment, menu)

            val searchItem = menu.findItem(R.id.pd_jentries_list_menu_search)
            searchView = searchItem.actionView as SearchView

            val pendingQuery = viewModel.pastDaySearchQueryJEntries.value
            if (pendingQuery.isNotEmpty()) {
                searchItem.expandActionView()
                searchView.setQuery(pendingQuery, false)
            }

            searchView.onQueryTextChanged { searchQuery ->
                viewModel.pastDaySearchQueryJEntries.value = searchQuery
            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
//                R.id.pd_jentries_list_menu_sort_by_date -> {
//                    viewModel.pastDaySortOrderQueryJEntries.value = SortOrder.BY_TIME
//                    true
//                }
//                R.id.pd_jentries_list_menu_sort_alphabetically -> {
//                    viewModel.pastDaySortOrderQueryJEntries.value = SortOrder.BY_NAME
//                    true
//                }
                else -> false
            }
        }
    }

    override fun onItemClick(journalEntry: JournalEntry) {
        viewModel.onPastDayJEntryClick(journalEntry, viewModel.formattedDate)
    }
}