package com.rajchenbergstudios.hoytask.ui.tasksset

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
import com.rajchenbergstudios.hoytask.R
import com.rajchenbergstudios.hoytask.data.taskset.TaskSet
import com.rajchenbergstudios.hoytask.databinding.FragmentTasksSetBinding
import com.rajchenbergstudios.hoytask.ui.createtaskset.CreateTaskSetDialogFragmentDirections
import com.rajchenbergstudios.hoytask.utils.OnQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskSetsListFragment : Fragment(R.layout.fragment_tasks_set), TaskSetsListAdapter.OnItemClickListener{

    private val viewModel: TasksSetsListViewModel by viewModels()

    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksSetBinding.bind(view)

        val tasksSetListAdapter = TaskSetsListAdapter(this)

        binding.apply {
            tasksSetRecyclerview.apply {
                tasksSetRecyclerview.layoutTasksListRecyclerview.apply {
                    adapter = tasksSetListAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                    setHasFixedSize(true)
                }
            }

            tasksSetFab.setOnClickListener {
                val action = CreateTaskSetDialogFragmentDirections.actionGlobalCreateTaskSetDialogFragment(task = null, origin = 1)
                findNavController().navigate(action)
            }
        }

        viewModel.taskSets.observe(viewLifecycleOwner) { taskSetsList ->
            tasksSetListAdapter.submitList(taskSetsList)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.taskSetEvent.collect{ event ->
                when (event) {
                    is TasksSetsListViewModel.TaskSetEvent.NavigateToDeleteAllSetsScreen -> {

                    }
                    is TasksSetsListViewModel.TaskSetEvent.NavigateToEditTaskSet -> {
                        val action = TaskSetsListFragmentDirections
                            .actionTaskSetsListFragmentToTasksSetEditListFragment(settitle = event.taskSet.title)
                        findNavController().navigate(action)
                    }
                }
            }
        }

        loadMenu()
    }

    private fun loadMenu() {

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object: MenuProvider{

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

                menuInflater.inflate(R.menu.menu_tasks_set_fragment, menu)

                val searchItem = menu.findItem(R.id.task_set_action_search)
                searchView = searchItem.actionView as SearchView

                val pendingQuery = viewModel.searchQuery.value
                if (pendingQuery.isNotEmpty()) {
                    searchItem.expandActionView()
                    searchView.setQuery(pendingQuery, false)
                }

                searchView.OnQueryTextChanged { searchQuery ->

                    viewModel.searchQuery.value = searchQuery
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.task_set_action_delete_all_sets -> {
                        viewModel.onDeleteAllSetsClick()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onItemClick(taskSet: TaskSet) {
        viewModel.onTaskSetSelected(taskSet)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }
}