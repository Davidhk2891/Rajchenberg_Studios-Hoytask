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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.data.prefs.SortOrder
import com.rajchenbergstudios.hoygenda.data.today.task.Task
import com.rajchenbergstudios.hoygenda.databinding.FragmentChildPdTasksListBinding
import com.rajchenbergstudios.hoygenda.ui.core.pastday.DaysDetailsFragmentDirections
import com.rajchenbergstudios.hoygenda.ui.core.pastday.SharedDayDetailsViewModel
import com.rajchenbergstudios.hoygenda.utils.HGDAViewStateUtils
import com.rajchenbergstudios.hoygenda.utils.exhaustive
import com.rajchenbergstudios.hoygenda.utils.onQueryTextChanged
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

const val TAG = "PDTasksListFragment"

@FlowPreview
@ExperimentalCoroutinesApi
class PDTasksListFragment : Fragment(R.layout.fragment_child_pd_tasks_list),
    PDTasksListAdapter.OnItemClickListener {

    private val sharedViewModel: SharedDayDetailsViewModel by viewModels()
    private lateinit var searchView: SearchView

    private lateinit var menuHost: MenuHost

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
        sharedViewModel.tasks?.observe(viewLifecycleOwner){ tasksList ->
            binding.apply {
                HGDAViewStateUtils.apply {
                    if (tasksList.isEmpty()) {
                        setViewVisibility(tasksListRecyclerview.layoutTasksListRecyclerview, visibility = View.INVISIBLE)
                        setViewVisibility(tasksListLayoutNoData.layoutNoDataLinearlayout, visibility = View.VISIBLE)
                    } else {
                        setViewVisibility(tasksListRecyclerview.layoutTasksListRecyclerview, visibility = View.VISIBLE)
                        setViewVisibility(tasksListLayoutNoData.layoutNoDataLinearlayout, visibility = View.INVISIBLE)
                        pdTasksListAdapter.submitList(tasksList)
                    }
                }
            }
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

    private fun loadPastDayTaskEventCollector() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            sharedViewModel.pastDayTaskEvent.collect { pastDayTaskEvent ->
                when (pastDayTaskEvent) {
                    is SharedDayDetailsViewModel.PastDayTaskEvent.NavigateToTaskDetailsScreen -> {
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

            val pendingQuery = sharedViewModel.pastDaySearchQuery.value
            if (pendingQuery.isNotEmpty()) {
                searchItem.expandActionView()
                searchView.setQuery(pendingQuery, false)
            }

            searchView.onQueryTextChanged { pastDaySearchQuery ->
                sharedViewModel.pastDaySearchQuery.value = pastDaySearchQuery
            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.pd_tasks_list_menu_sort_by_date -> {
                    sharedViewModel.pastDaySortOrderQuery.value = SortOrder.BY_TIME
                    true
                }
                R.id.pd_tasks_list_menu_sort_alphabetically -> {
                    sharedViewModel.pastDaySortOrderQuery.value = SortOrder.BY_NAME
                    true
                }
                else -> false
            }
        }
    }

    override fun onItemClick(task: Task) {
        sharedViewModel.onPastDayTaskClick(task, sharedViewModel.formattedDate)
    }
}