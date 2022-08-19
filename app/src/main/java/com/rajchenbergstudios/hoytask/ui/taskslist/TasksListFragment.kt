package com.rajchenbergstudios.hoytask.ui.taskslist

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajchenbergstudios.hoytask.R
import com.rajchenbergstudios.hoytask.databinding.FragmentTasksListBinding
import com.rajchenbergstudios.hoytask.util.OnQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TasksListFragment : Fragment(R.layout.fragment_tasks_list){

    private val viewModel: TasksListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksListBinding.bind(view)

        val tasksListAdapter = TasksListAdapter()

        binding.apply {
            tasksListRecyclerview.apply {

                adapter = tasksListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.tasks.observe(viewLifecycleOwner){ tasksList ->

            tasksListAdapter.submitList(tasksList)
        }

        loadMenu()
    }

    private fun loadMenu(){

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object: MenuProvider{

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

                menuInflater.inflate(R.menu.menu_tasks_list_fragment, menu)

                val searchItem = menu.findItem(R.id.tasks_list_menu_search)
                val searchView = searchItem.actionView as SearchView

                searchView.OnQueryTextChanged{ searchQuery ->

                    viewModel.searchQuery.value = searchQuery
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.tasks_list_menu_sort_by_date -> {
                        viewModel.sortOrderQuery.value = SortOrder.BY_DATE
                        true
                    }
                    R.id.tasks_list_menu_sort_by_name -> {
                        viewModel.sortOrderQuery.value = SortOrder.BY_NAME
                        true
                    }
                    R.id.tasks_list_menu_hide_completed -> {
                        menuItem.isChecked = !menuItem.isChecked
                        viewModel.hideCompletedQuery.value = menuItem.isChecked
                        true
                    }
                    R.id.tasks_list_menu_delete_completed -> {

                        true
                    }
                    else -> false
                }
            }
        })
    }
}