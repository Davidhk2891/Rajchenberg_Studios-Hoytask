package com.rajchenbergstudios.hoygenda.ui.today.todayjentrieslist

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
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntry
import com.rajchenbergstudios.hoygenda.databinding.FragmentChildJournalEntriesListBinding
import com.rajchenbergstudios.hoygenda.ui.today.TodayFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TJEntriesListFragment : Fragment(R.layout.fragment_child_journal_entries_list), TJEntriesListAdapter.OnItemClickListener {

    private val viewModel: TJEntriesListViewModel by viewModels()
    private lateinit var searchView: SearchView
    private lateinit var menuHost: MenuHost

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadMenu()
        val binding = FragmentChildJournalEntriesListBinding.bind(view)
        val jEntriesAdapter = TJEntriesListAdapter(this)

        binding.apply {
            journalEntriesListRecyclerview.layoutTasksListRecyclerview.apply {
                adapter = jEntriesAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.entries.observe(viewLifecycleOwner){ jEntriesList ->
            jEntriesAdapter.submitList(jEntriesList)
        }

        loadJEntriesEventCollector()
    }

    private fun loadJEntriesEventCollector() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.jEntriesEvent.collect { event ->
                when (event) {
                    is TJEntriesListViewModel.JEntriesEvent.NavigateToEditJEntryScreen -> {
                        val action = TodayFragmentDirections
                            .actionTodayFragmentToJEntryAddEditFragment(title = "Edit entry", jentry = event.journalEntry)
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    private fun loadMenu(){
        menuHost = requireActivity()
        menuHost.addMenuProvider(JEntriesMenuProvider(), viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private inner class JEntriesMenuProvider : MenuProvider{
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menu.clear()
            menuInflater.inflate(R.menu.menu_jentries_list_fragment, menu)

            val searchItem = menu.findItem(R.id.jentries_list_menu_search)
            searchView = searchItem.actionView as SearchView

//                val pendingQuery = viewModel.searchQuery.value
//                if (pendingQuery != null && pendingQuery.isNotEmpty()) {
//                    searchItem.expandActionView()
//                    searchView.setQuery(pendingQuery, false)
//                }
//
//                searchView.OnQueryTextChanged{ searchQuery ->
//                    viewModel.searchQuery.value = searchQuery
//                }
//
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.jentries_list_menu_sort_by_time -> {
//                        viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                    true
                }
                R.id.jentries_list_menu_sort_alphabetically -> {
//                        viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                    true
                }
                R.id.jentries_list_menu_delete_all -> {
//                        viewModel.onDeleteAllClick()
                    true
                }
                else -> false
            }
        }
    }

    override fun onItemClick(journalEntry: JournalEntry) {
        viewModel.onJEntrySelected(journalEntry)
    }

    override fun onItemLongClick(journalEntry: JournalEntry) {

    }
}