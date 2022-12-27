package com.rajchenbergstudios.hoygenda.ui.pastday.daydetailstaskslist

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.databinding.FragmentChildPdTasksListBinding
import com.rajchenbergstudios.hoygenda.ui.pastday.SharedDayDetailsViewModel
import com.rajchenbergstudios.hoygenda.utils.HGDAViewStateUtils
import com.rajchenbergstudios.hoygenda.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext

const val TAG = "PDTasksListFragment"

@ExperimentalCoroutinesApi
class PDTasksListFragment : Fragment(R.layout.fragment_child_pd_tasks_list) {

    private val sharedViewModel: SharedDayDetailsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentChildPdTasksListBinding.bind(view)

        val pdTasksListAdapter = PDTasksListAdapter()

        binding.apply {

            tasksListRecyclerview.layoutTasksListRecyclerview.apply {
                adapter = pdTasksListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        loadMenu()
        loadObservable(sharedViewModel, binding, pdTasksListAdapter)
    }

    private fun loadObservable(viewModel: SharedDayDetailsViewModel, binding: FragmentChildPdTasksListBinding, pdTasksListAdapter: PDTasksListAdapter) {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            withContext(Dispatchers.IO) {
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
    }

    private fun loadMenu(){
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(TasksMenuProvider(), viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private inner class TasksMenuProvider : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menu.clear()
            // menuInflater.inflate(R.menu.menu_tasks_list_fragment, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                else -> false
            }
        }
    }
}