package com.rajchenbergstudios.hoytask.ui.taskssetedit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rajchenbergstudios.hoytask.R
import com.rajchenbergstudios.hoytask.databinding.FragmentTasksInSetBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksInSetListFragment : Fragment(R.layout.fragment_tasks_in_set) {

    private val viewModel: TasksInSetListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksInSetBinding.bind(view)

        val tasksInSetListAdapter = TasksInSetListAdapter()

        binding.apply {
            tasksSetDetailsRecyclerview.layoutTasksListRecyclerview.apply {
                adapter = tasksInSetListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
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
                    val taskInSet = tasksInSetListAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTaskInSetSwiped(taskInSet)
                }
            }).attachToRecyclerView(tasksSetDetailsRecyclerview.layoutTasksListRecyclerview)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tasksInSetsEvent.collect { event ->
                when (event) {
                    is TasksInSetListViewModel.TaskInSetEvent.ShowUndoDeleteTaskMessage -> {
                        Snackbar
                            .make(requireView(), "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO"){
                                viewModel.onUndoDeleteClick(event.taskInSet)
                            }
                            .show()
                    }
                }
            }
        }

        viewModel.tasksSet?.observe(viewLifecycleOwner) { tasksInSetList ->
            tasksInSetListAdapter.submitList(tasksInSetList)
        }
    }
}