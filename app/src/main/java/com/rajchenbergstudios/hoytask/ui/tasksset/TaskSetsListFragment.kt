package com.rajchenbergstudios.hoytask.ui.tasksset

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajchenbergstudios.hoytask.R
import com.rajchenbergstudios.hoytask.databinding.FragmentTasksSetBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskSetsListFragment : Fragment(R.layout.fragment_tasks_set){

    private val viewModel: TasksSetsListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksSetBinding.bind(view)

        val tasksSetListAdapter = TaskSetsListAdapter()

        binding.apply {
            tasksSetRecyclerview.apply {
                tasksSetRecyclerview.layoutTasksListRecyclerview.apply {
                    adapter = tasksSetListAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                    setHasFixedSize(true)
                }
            }
        }

        viewModel.taskSets.observe(viewLifecycleOwner) { taskSetsList ->
            tasksSetListAdapter.submitList(taskSetsList)
        }
    }
}