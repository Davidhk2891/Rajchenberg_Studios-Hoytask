package com.rajchenbergstudios.hoygenda.ui.today.todayjentrieslist

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.data.prefs.SortOrder
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntry
import com.rajchenbergstudios.hoygenda.databinding.FragmentChildTJournalEntriesListBinding
import com.rajchenbergstudios.hoygenda.ui.today.TodayFragmentDirections
import com.rajchenbergstudios.hoygenda.ui.today.todaytaskslist.TTasksListFragmentDirections
import com.rajchenbergstudios.hoygenda.utils.HGDAViewStateUtils
import com.rajchenbergstudios.hoygenda.utils.OnQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TJEntriesListFragment : Fragment(R.layout.fragment_child_t_journal_entries_list), TJEntriesListAdapter.OnItemClickListener {

    private val viewModel: TJEntriesListViewModel by viewModels()
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadMenu()
        val binding = FragmentChildTJournalEntriesListBinding.bind(view)
        val jEntriesAdapter = TJEntriesListAdapter(this)

        binding.apply {

            journalEntriesListRecyclerview.layoutTasksListRecyclerview.apply {
                adapter = jEntriesAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = jEntriesAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onJEntrySwiped(task)
                }
            }).attachToRecyclerView(journalEntriesListRecyclerview.layoutTasksListRecyclerview)
        }

        viewModel.entries.observe(viewLifecycleOwner){ jEntriesList ->
            jEntriesAdapter.submitList(jEntriesList)
        }

        loadObservable(binding, jEntriesAdapter)
        loadJEntriesEventCollector()
    }

    private fun loadJEntriesEventCollector() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.jEntriesEvent.collect { event ->
                when (event) {
                    is TJEntriesListViewModel.JEntriesEvent.ShowUndoDeleteJEntryMessage -> {
                        Snackbar
                            .make(requireView(), "Entry deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO"){
                                viewModel.onUndoDeleteClick(event.journalEntry)
                            }
                            .show()
                    }
                    is TJEntriesListViewModel.JEntriesEvent.NavigateToEditJEntryScreen -> {
                        val action = TodayFragmentDirections
                            .actionTodayFragmentToJEntryAddEditFragment(title = "Edit entry", jentry = event.journalEntry, origin = 1)
                        findNavController().navigate(action)
                    }
                    TJEntriesListViewModel.JEntriesEvent.NavigateToDeleteAllScreen -> {
                        val action = TJEntriesListFragmentDirections.actionGlobalTasksDeleteAllDialogFragment(origin = 4)
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    private fun loadObservable(binding: FragmentChildTJournalEntriesListBinding, TJEntriesListAdapter: TJEntriesListAdapter) {
        viewModel.entries.observe(viewLifecycleOwner){ jEntriesList ->
            binding.apply {
                HGDAViewStateUtils.apply {
                    if (jEntriesList.isEmpty()) {
                        setViewVisibility(journalEntriesListRecyclerview.layoutTasksListRecyclerview, visibility = View.INVISIBLE)
                        setViewVisibility(journalEntriesListLayoutNoData.layoutNoDataLinearlayout, visibility = View.VISIBLE)
                    } else {
                        setViewVisibility(journalEntriesListRecyclerview.layoutTasksListRecyclerview, visibility = View.VISIBLE)
                        setViewVisibility(journalEntriesListLayoutNoData.layoutNoDataLinearlayout, visibility = View.INVISIBLE)
                        TJEntriesListAdapter.submitList(jEntriesList)
                    }
                }
            }
        }
    }

    private fun loadMenu(){
        // 1st attempt: Move both menu providers to TodayFragment, and depending on which frag you stand, add the right menu provider
        val menuHost = requireActivity()
        menuHost.addMenuProvider(JEntriesMenuProvider(), viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private inner class JEntriesMenuProvider : MenuProvider{
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menu.clear()
            menuInflater.inflate(R.menu.menu_t_jentries_list_fragment, menu)

            val searchItem = menu.findItem(R.id.jentries_list_menu_search)
            searchView = searchItem.actionView as SearchView

            val pendingQuery = viewModel.searchQuery.value
            if (pendingQuery != null && pendingQuery.isNotEmpty()) {
                searchItem.expandActionView()
                searchView.setQuery(pendingQuery, false)
            }

            searchView.OnQueryTextChanged{ searchQuery ->
                viewModel.searchQuery.value = searchQuery
            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.jentries_list_menu_sort_by_time -> {
                        viewModel.onSortOrderSelected(SortOrder.BY_TIME)
                    true
                }
                R.id.jentries_list_menu_sort_alphabetically -> {
                        viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                    true
                }
                R.id.jentries_list_menu_delete_all -> {
                        viewModel.onDeleteAllClick()
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