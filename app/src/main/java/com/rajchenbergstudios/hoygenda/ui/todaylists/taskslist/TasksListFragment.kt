package com.rajchenbergstudios.hoygenda.ui.todaylists.taskslist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
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
import com.rajchenbergstudios.hoygenda.ui.todaylists.TodayFragment
import com.rajchenbergstudios.hoygenda.ui.todaylists.TodayFragmentDirections
import com.rajchenbergstudios.hoygenda.utils.HGDAViewStateUtils
import com.rajchenbergstudios.hoygenda.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TasksListFragment : Fragment(R.layout.fragment_child_tasks_list),
    TasksListAdapter.OnItemClickListener {

    private val viewModel: TasksListViewModel by viewModels()
    private lateinit var callback: TasksListFragListener

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
                    is TasksListViewModel.TaskEvent.NavigateToEditTaskScreen -> {
                        val action = TodayFragmentDirections
                            .actionTodayFragmentToTaskAddEditFragment(task = event.task, title = "Edit task", taskinset = null, origin = 1)
                        findNavController().navigate(action)
                    }
                    is TasksListViewModel.TaskEvent.NavigateToAddTaskToSetBottomSheet -> {
                        val action = TasksListFragmentDirections.actionGlobalSetBottomSheetDialogFragment(task = event.task, origin = 1)
                        findNavController().navigate(action)
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
                }.exhaustive
            }
        }
    }

    private fun getSearchQueryData() {
        // Arrange search query data from viewModel, then pass
        // that value to the interface which TodayFragment will implement in menu
        // OR
        // Try to add the searchItem menuItem from this fragment, to the TodayFragment menu
    }

    fun setListener(listener: TodayFragment) {
        callback = listener
    }

    interface TasksListFragListener{
//        fun onSearchViewEngaged(searchItem: MenuItem){
//            val searchView: SearchView = searchItem.actionView as SearchView
//
//            val pendingQuery = viewModel.searchQuery.value
//            if (pendingQuery != null && pendingQuery.isNotEmpty()) {
//                searchItem.expandActionView()
//                searchView.setQuery(pendingQuery, false)
//            }
//        }
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
}