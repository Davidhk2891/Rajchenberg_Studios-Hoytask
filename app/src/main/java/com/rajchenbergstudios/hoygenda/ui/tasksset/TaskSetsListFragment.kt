package com.rajchenbergstudios.hoygenda.ui.tasksset

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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.data.taskset.TaskSet
import com.rajchenbergstudios.hoygenda.databinding.FragmentTasksSetBinding
import com.rajchenbergstudios.hoygenda.ui.createtaskset.CreateTaskSetDialogFragmentDirections
import com.rajchenbergstudios.hoygenda.utils.HGDAAnimationUtils
import com.rajchenbergstudios.hoygenda.utils.HGDAViewStateUtils
import com.rajchenbergstudios.hoygenda.utils.OnQueryTextChanged
import com.rajchenbergstudios.hoygenda.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskSetsListFragment : Fragment(R.layout.fragment_tasks_set), TaskSetsListAdapter.OnItemClickListener{

    private val viewModel: TasksSetsListViewModel by viewModels()

    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startShimmerView()
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
                    val set = tasksSetListAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onSetSwiped(set)
                }
            }).attachToRecyclerView(tasksSetRecyclerview.layoutTasksListRecyclerview)
        }

        viewModel.taskSets.observe(viewLifecycleOwner) { taskSetsList ->
            binding.apply {
                HGDAViewStateUtils.apply {
                    if (taskSetsList.isEmpty()) {
                        stopShimmerView()
                        setViewVisibility(tasksSetRecyclerview.layoutTasksListRecyclerview, visibility = View.INVISIBLE)
                        setViewVisibility(tasksSetLayoutNoData.layoutNoDataLinearlayout, visibility = View.VISIBLE)
                        setViewVisibility(tasksSetLayoutNoData.layoutNoDataImageview, visibility = View.GONE)
                        tasksSetLayoutNoData.layoutNoDataTextview.text = getString(R.string.you_don_t_have_any_sets)
                    } else {
                        stopShimmerView()
                        setViewVisibility(tasksSetRecyclerview.layoutTasksListRecyclerview, visibility = View.VISIBLE)
                        setViewVisibility(tasksSetLayoutNoData.layoutNoDataLinearlayout, visibility = View.INVISIBLE)
                        tasksSetListAdapter.submitList(taskSetsList)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.taskSetEvent.collect{ event ->
                when (event) {
                    is TasksSetsListViewModel.TaskSetEvent.NavigateToDeleteAllSetsScreen -> {
                        val action = TaskSetsListFragmentDirections
                            .actionGlobalTasksDeleteAllDialogFragment(origin = 2)
                        findNavController().navigate(action)
                    }
                    is TasksSetsListViewModel.TaskSetEvent.NavigateToEditTaskSet -> {
                        val action = TaskSetsListFragmentDirections
                            .actionTaskSetsListFragmentToTasksSetEditListFragment(settitle = event.taskSet.title)
                        findNavController().navigate(action)
                    }
                    is TasksSetsListViewModel.TaskSetEvent.ShowUndoDeleteSetMessage -> {
                        Snackbar
                            .make(requireView(), "Set deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO"){
                                viewModel.onUndoDeleteClick(event.taskSet, event.tasksInSetList)
                            }
                            .show()
                    }
                }.exhaustive
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

    private fun startShimmerView() { HGDAAnimationUtils.startShimmerView(requireActivity(), R.id.task_set_shimmerframelayout) }

    private fun stopShimmerView() { HGDAAnimationUtils.stopShimmerView(requireActivity(), R.id.task_set_shimmerframelayout) }

    override fun onItemClick(taskSet: TaskSet) {
        viewModel.onTaskSetSelected(taskSet)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }
}