package com.rajchenbergstudios.hoygenda.ui.core.pastday.daydetailstaskslist

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
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.data.prefs.SortOrder
import com.rajchenbergstudios.hoygenda.data.today.task.Task
import com.rajchenbergstudios.hoygenda.databinding.FragmentChildPdTasksListBinding
import com.rajchenbergstudios.hoygenda.ui.core.pastday.DaysDetailsFragmentDirections
import com.rajchenbergstudios.hoygenda.ui.core.pastday.DayDetailsViewModel
import com.rajchenbergstudios.hoygenda.utils.HGDAViewStateUtils
import com.rajchenbergstudios.hoygenda.utils.Logger
import com.rajchenbergstudios.hoygenda.utils.exhaustive
import com.rajchenbergstudios.hoygenda.utils.onQueryTextChanged
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

const val TAG = "PDTasksListFragment"

@FlowPreview
@ExperimentalCoroutinesApi
class PDTasksListFragment : Fragment(R.layout.fragment_child_pd_tasks_list),
    PDTasksListAdapter.OnItemClickListener {

    private val viewModel: DayDetailsViewModel by viewModels()
    private lateinit var searchView: SearchView

    private lateinit var menuHost: MenuHost

    private lateinit var tasksObserver: Observer<List<Task>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentChildPdTasksListBinding.bind(view)
        val pdTasksListAdapter = PDTasksListAdapter(this)

        binding.apply {

            tasksListRecyclerview.layoutTasksListRecyclerview.apply {
                adapter = pdTasksListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        loadMenu()
        loadObservable(binding, pdTasksListAdapter)
        loadPastDayTaskEventCollector()
    }

    private fun loadObservable(binding: FragmentChildPdTasksListBinding, pdTasksListAdapter: PDTasksListAdapter) {
        tasksObserver = Observer{ tasksList ->
            // This line below does not run sorting alphabetically second time I come back to the fragment
            Logger.i(TAG, "Sort bug: onMenuItemSelected", "THIS LINE RAN")
            binding.apply {
                HGDAViewStateUtils.apply {
                    if (tasksList.isEmpty()) {
                        setViewVisibility(tasksListRecyclerview.layoutTasksListRecyclerview, visibility = View.INVISIBLE)
                        setViewVisibility(tasksListLayoutNoData.layoutNoDataLinearlayout, visibility = View.VISIBLE)
                    } else {
                        setViewVisibility(tasksListRecyclerview.layoutTasksListRecyclerview, visibility = View.VISIBLE)
                        setViewVisibility(tasksListLayoutNoData.layoutNoDataLinearlayout, visibility = View.INVISIBLE)
                        Logger.i(TAG, "Sort bug: loadObservable", "--------------------------")
                        Logger.i(TAG, "Sort bug: onMenuItemSelected", "sort current val IN OBSERVER is: ${viewModel.pastDaySortOrderQuery.value}")
                        Logger.i(TAG, "Sort bug: loadObservable", "list result is: $tasksList")
                        pdTasksListAdapter.submitList(tasksList)
                    }
                }
            }
        }
        viewModel.tasks?.observe(viewLifecycleOwner, tasksObserver)
    }

// 2 more examples for collecting flow are at the bottom of the file

    private fun loadPastDayTaskEventCollector() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.pastDayTaskEvent.collect { pastDayTaskEvent ->
                when (pastDayTaskEvent) {
                    is DayDetailsViewModel.PastDayTaskEvent.NavigateToTaskDetailsScreen -> {
                        val action = DaysDetailsFragmentDirections.actionDaysDetailsFragmentToTaskAddEditFragment(task = pastDayTaskEvent.task
                            , title = "Task from ${pastDayTaskEvent.date}", taskinset = null, origin = 3)
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }
    }

    private fun loadMenu(){
        menuHost = requireActivity()
        menuHost.addMenuProvider(TasksMenuProvider(), viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private inner class TasksMenuProvider : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menu.clear()
            menuInflater.inflate(R.menu.menu_pd_tasks_list_fragment, menu)

            val searchItem = menu.findItem(R.id.pd_tasks_list_menu_search)
            searchView = searchItem.actionView as SearchView

            val pendingQuery = viewModel.pastDaySearchQuery.value
            if (pendingQuery.isNotEmpty()) {
                searchItem.expandActionView()
                searchView.setQuery(pendingQuery, false)
            }

            searchView.onQueryTextChanged { pastDaySearchQuery ->
                viewModel.pastDaySearchQuery.value = pastDaySearchQuery
            }

//            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//                viewModel.pastDaySortOrderQuery.value = SortOrder.BY_TIME
//
//            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
//                R.id.pd_tasks_list_menu_sort_by_date -> {
//                    viewModel.pastDaySortOrderQuery.value = SortOrder.BY_TIME
//                    Logger.i(TAG, "Sort bug: onMenuItemSelected", "life cycle current state is: ${viewLifecycleOwner.lifecycle.currentState}")
//                    Logger.i(TAG, "Sort bug: onMenuItemSelected", "sort current val AFTER PRESS is: ${viewModel.pastDaySortOrderQuery.value}")
//                    Logger.i(TAG, "Sort bug: loadObservable", "--------------------------")
//                    true
//                }
//                R.id.pd_tasks_list_menu_sort_alphabetically -> {
//                    viewModel.pastDaySortOrderQuery.value = SortOrder.BY_NAME
//                    Logger.i(TAG, "Sort bug: onMenuItemSelected", "life cycle current state is: ${viewLifecycleOwner.lifecycle.currentState}")
//                    Logger.i(TAG, "Sort bug: onMenuItemSelected", "sort current val AFTER PRESS is: ${viewModel.pastDaySortOrderQuery.value}")
//                    Logger.i(TAG, "Sort bug: loadObservable", "--------------------------")
//                    true
//                }
                else -> false
            }
        }
    }

    override fun onItemClick(task: Task) {
        viewModel.onPastDayTaskClick(task, viewModel.formattedDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.tasks?.removeObserver(tasksObserver)
    }
}

//        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//            sharedViewModel.filteredTasksFlow?.collect { remainingTasks ->
//                // Update UI with remainingTasks
//                binding.apply {
//                    HGDAViewStateUtils.apply {
//                        if (remainingTasks.isEmpty()) {
//                            setViewVisibility(tasksListRecyclerview.layoutTasksListRecyclerview, visibility = View.INVISIBLE)
//                            setViewVisibility(tasksListLayoutNoData.layoutNoDataLinearlayout, visibility = View.VISIBLE)
//                        } else {
//                            setViewVisibility(tasksListRecyclerview.layoutTasksListRecyclerview, visibility = View.VISIBLE)
//                            setViewVisibility(tasksListLayoutNoData.layoutNoDataLinearlayout, visibility = View.INVISIBLE)
//                            pdTasksListAdapter.submitList(remainingTasks)
//                        }
//                    }
//                }
//            }
//        }

//    private fun loadObservable(binding: FragmentChildPdTasksListBinding, pdTasksListAdapter: PDTasksListAdapter) {
//        sharedViewModel.filteredTasksFlow?.onEach { remainingTasks ->
//            Logger.i(TAG, "loadObservable", "Remaining tasks: $remainingTasks")
//            binding.apply {
//                HGDAViewStateUtils.apply {
//                    if (remainingTasks.isEmpty()) {
//                        setViewVisibility(tasksListRecyclerview.layoutTasksListRecyclerview, visibility = View.INVISIBLE)
//                        setViewVisibility(tasksListLayoutNoData.layoutNoDataLinearlayout, visibility = View.VISIBLE)
//                        Logger.i(TAG, "loadObservable", "No tasks to show with the provided query")
//                    } else {
//                        setViewVisibility(tasksListRecyclerview.layoutTasksListRecyclerview, visibility = View.VISIBLE)
//                        setViewVisibility(tasksListLayoutNoData.layoutNoDataLinearlayout, visibility = View.INVISIBLE)
//                        Logger.i(TAG, "loadObservable", "Remaining tasks right before they are shown: $remainingTasks")
//                        pdTasksListAdapter.submitList(remainingTasks)
//                    }
//                }
//            }
//        }?.launchIn(viewLifecycleOwner.lifecycleScope)
//    }