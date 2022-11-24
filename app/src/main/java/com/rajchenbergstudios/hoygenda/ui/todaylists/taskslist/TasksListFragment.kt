package com.rajchenbergstudios.hoygenda.ui.todaylists.taskslist

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.data.today.task.Task
import com.rajchenbergstudios.hoygenda.databinding.FragmentChildTasksListBinding
import com.rajchenbergstudios.hoygenda.utils.HGDAViewStateUtils
import com.rajchenbergstudios.hoygenda.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TasksListFragment : Fragment(R.layout.fragment_child_tasks_list),
    TasksListAdapter.OnItemClickListener {

    private val viewModel: TasksListViewModel by viewModels()
    private lateinit var callback: OnActionTakenFromListFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentChildTasksListBinding.bind(view)
        val tasksListAdapter = TasksListAdapter(this)

        binding.apply {

            tasksListRecyclerview.layoutTasksListRecyclerview.apply {
                adapter = tasksListAdapter
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
                    val task = tasksListAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTaskSwiped(task)
                }
            }).attachToRecyclerView(tasksListRecyclerview.layoutTasksListRecyclerview)
        }

        loadObservable(binding, tasksListAdapter)
        loadTasksEventCollector()
        getFragmentResultListeners()
    }

    private fun loadObservable(binding: FragmentChildTasksListBinding, tasksListAdapter: TasksListAdapter) {
        viewModel.tasks.observe(viewLifecycleOwner){ tasksList ->
            binding.apply {
                HGDAViewStateUtils.apply {
                    if (tasksList.isEmpty()) {
                        setViewVisibility(tasksListRecyclerview.layoutTasksListRecyclerview, visibility = View.INVISIBLE)
                        setViewVisibility(tasksListLayoutNoData.layoutNoDataLinearlayout, visibility = View.VISIBLE)
                    } else {
                        setViewVisibility(tasksListRecyclerview.layoutTasksListRecyclerview, visibility = View.VISIBLE)
                        setViewVisibility(tasksListLayoutNoData.layoutNoDataLinearlayout, visibility = View.INVISIBLE)
                        tasksListAdapter.submitList(tasksList)
                    }
                }
            }
        }
    }

    private fun loadTasksEventCollector() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tasksEvent.collect { event ->
                when (event) {
                    is TasksListViewModel.TaskEvent.ShowUndoDeleteTaskMessage -> {
                        Snackbar
                            .make(requireView(), "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO"){
                                viewModel.onUndoDeleteClick(event.task)
                            }
                            .show()
                    }
                    is TasksListViewModel.TaskEvent.NavigateToAddTaskScreen -> {
                        val action = TasksListFragmentDirections
                            .actionTasksListFragmentToTaskAddEditFragment(task = null, title = "Add task", taskinset = null, origin = 1)
                        findNavController().navigate(action)
                    }
                    is TasksListViewModel.TaskEvent.NavigateToEditTaskScreen -> {
                        val action = TasksListFragmentDirections
                            .actionTasksListFragmentToTaskAddEditFragment(task = event.task, title = "Edit task", taskinset = null, origin = 1)
                        findNavController().navigate(action)
                    }
                    is TasksListViewModel.TaskEvent.NavigateToAddTaskToSetBottomSheet -> {
                        val action = TasksListFragmentDirections.actionGlobalSetBottomSheetDialogFragment(task = event.task, origin = 1)
                        findNavController().navigate(action)
                    }
                    is TasksListViewModel.TaskEvent.NavigateToAddTasksFromSetBottomSheet -> {
                        val action = TasksListFragmentDirections.actionGlobalSetBottomSheetDialogFragment(task = null, origin = 2)
                        findNavController().navigate(action)
                    }
                    is TasksListViewModel.TaskEvent.ShowTaskSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is TasksListViewModel.TaskEvent.NavigateToDeleteAllCompletedScreen -> {
                        val action = TasksListFragmentDirections
                            .actionGlobalTasksDeleteAllDialogFragment(origin = 1)
                        findNavController().navigate(action)
                    }
                    is TasksListViewModel.TaskEvent.NavigateToDeleteAllScreen -> {
                        val action = TasksListFragmentDirections
                            .actionGlobalTasksDeleteAllDialogFragment(origin = 3)
                        findNavController().navigate(action)
                    }
                    is TasksListViewModel.TaskEvent.ShowTaskSavedInNewOrOldSetConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg.toString(), Snackbar.LENGTH_LONG).show()
                    }
                    is TasksListViewModel.TaskEvent.ShowTaskAddedFromSetConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg.toString(), Snackbar.LENGTH_LONG).show()
                        callback.onTaskAddedFromSetConfirmationMessage()
                    }
                }.exhaustive
            }
        }
    }

    private fun getFragmentResultListeners() {
        setFragmentResultListener("add_edit_request"){_, bundle ->
            val result = bundle.getInt("add_edit_result")
            onFragmentResult(result)
        }
        setFragmentResultListener("create_set_request_2"){_, bundle ->
            val result = bundle.getInt("create_set_result_2")
            onFragmentResult(result)
        }
        setFragmentResultListener("task_added_to_set_request"){_, bundle ->
            val result = bundle.getInt("task_added_to_set_result")
            val message = bundle.getString("task_added_to_set_message")
            onFragmentResult(result, message)
        }
        setFragmentResultListener("task_added_from_set_request"){_, bundle ->
            val result = bundle.getInt("task_added_from_set_result")
            val message = bundle.getString("task_added_from_set_message")
            onFragmentResult(result, message)
        }
    }

    private fun onFragmentResult(result: Int, message: String? = ""){
        viewModel.onFragmentResult(result, message)
    }

    interface OnActionTakenFromListFragment{
        fun onTaskAddedFromSetConfirmationMessage()
        fun onChildFragmentPaused(mainFabClicked: Boolean)
    }

    override fun onItemClick(task: Task) {
        viewModel.onTaskSelected(task)
    }

    override fun onItemLongClick(task: Task) {
        viewModel.onTaskLongSelected(task)
    }

    override fun onCheckboxClick(task: Task, isChecked: Boolean) {
        viewModel.onTaskCheckedChanged(task, isChecked)
    }

    override fun onAttach(context: Context) {
        callback  = context as OnActionTakenFromListFragment
    }

    override fun onPause() {
        super.onPause()
        callback.onChildFragmentPaused(false)
    }
}