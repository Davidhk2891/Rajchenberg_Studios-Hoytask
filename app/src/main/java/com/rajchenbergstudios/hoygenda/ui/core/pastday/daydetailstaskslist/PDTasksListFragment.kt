package com.rajchenbergstudios.hoygenda.ui.core.pastday.daydetailstaskslist

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.data.today.task.Task
import com.rajchenbergstudios.hoygenda.databinding.FragmentChildPdTasksListBinding
import com.rajchenbergstudios.hoygenda.ui.core.pastday.DaysDetailsFragmentDirections
import com.rajchenbergstudios.hoygenda.ui.core.pastday.SharedDayDetailsViewModel
import com.rajchenbergstudios.hoygenda.utils.HGDAViewStateUtils
import com.rajchenbergstudios.hoygenda.utils.Logger
import com.rajchenbergstudios.hoygenda.utils.exhaustive
import kotlinx.coroutines.ExperimentalCoroutinesApi

const val TAG = "PDTasksListFragment"

@ExperimentalCoroutinesApi
class PDTasksListFragment : Fragment(R.layout.fragment_child_pd_tasks_list),
    PDTasksListAdapter.OnItemClickListener {

    private val sharedViewModel: SharedDayDetailsViewModel by viewModels()

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
        loadObservable(sharedViewModel, binding, pdTasksListAdapter)
        loadPastDayTaskEventCollector()
    }

    private fun loadObservable(viewModel: SharedDayDetailsViewModel, binding: FragmentChildPdTasksListBinding, pdTasksListAdapter: PDTasksListAdapter) {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            binding.apply {
                HGDAViewStateUtils.apply {
                    Logger.i(TAG, "loadObservable", "some tasks data: ${viewModel.dayMonth} - ${viewModel.dayYear}: ${viewModel.tasksList}")
                    if (viewModel.tasksList?.isEmpty() == true) {
                        setViewVisibility(tasksListRecyclerview.layoutTasksListRecyclerview, visibility = View.INVISIBLE)
                        setViewVisibility(tasksListLayoutNoData.layoutNoDataLinearlayout, visibility = View.VISIBLE)
                    } else {
                        setViewVisibility(tasksListRecyclerview.layoutTasksListRecyclerview, visibility = View.VISIBLE)
                        setViewVisibility(tasksListLayoutNoData.layoutNoDataLinearlayout, visibility = View.INVISIBLE)
                        pdTasksListAdapter.submitList(viewModel.tasksList)
                    }
                }
            }
        }
    }

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
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                else -> false
            }
        }
    }

    override fun onItemClick(task: Task) {
        sharedViewModel.onPastDayTaskClick(task, sharedViewModel.formattedDate)
    }

    override fun onPause() {
        super.onPause()
    }
}