package com.rajchenbergstudios.hoygenda.ui.todaylists.journalentrieslist

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.databinding.FragmentChildJournalEntriesListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class JEntriesListFragment : Fragment(R.layout.fragment_child_journal_entries_list) {

    private val viewModel: JEntriesListViewModel by viewModels()
    private lateinit var searchView: SearchView
    private lateinit var menuHost: MenuHost

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadMenu()
        val binding = FragmentChildJournalEntriesListBinding.bind(view)
        val jEntriesAdapter = JEntriesListAdapter()

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
//                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//                    menu.findItem(R.id.tasks_list_menu_hide_completed).isChecked =
//                        viewModel.preferencesFlow.first().hideCompleted
//                }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.jentries_list_menu_sort_by_date -> {
//                        viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                    true
                }
                R.id.jentries_list_menu_sort_by_name -> {
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
}