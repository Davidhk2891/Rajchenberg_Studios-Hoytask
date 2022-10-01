package com.rajchenbergstudios.hoytask.ui.taskssetedit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajchenbergstudios.hoytask.R
import com.rajchenbergstudios.hoytask.databinding.FragmentTasksSetEditBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksInSetListFragment : Fragment(R.layout.fragment_tasks_set_edit) {

    private val viewModel: TasksInSetListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksSetEditBinding.bind(view)

        val tasksInSetListAdapter = TasksInSetListAdapter()

        binding.apply {
            tasksSetDetailsRecyclerview.layoutTasksListRecyclerview.apply {
                adapter = tasksInSetListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.tasksSet?.observe(viewLifecycleOwner) { tasksInSetList ->
            // The list to submit is to be that of only the actual tasks in the set, not the whole TaskSet item
            tasksInSetListAdapter.submitList(tasksInSetList)
        }
    }
}