package com.rajchenbergstudios.hoytask.ui.taskslist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajchenbergstudios.hoytask.R
import com.rajchenbergstudios.hoytask.databinding.FragmentTasksListBinding
import dagger.hilt.android.AndroidEntryPoint

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
    }
}