package com.rajchenbergstudios.hoygenda.ui.core.today.todaytaskslist

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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.data.prefs.SortOrder
import com.rajchenbergstudios.hoygenda.data.today.task.Task
import com.rajchenbergstudios.hoygenda.databinding.FragmentChildTTasksListBinding
import com.rajchenbergstudios.hoygenda.ui.core.today.TodayFragmentDirections
import com.rajchenbergstudios.hoygenda.utils.HGDAViewStateUtils
import com.rajchenbergstudios.hoygenda.utils.OnQueryTextChanged
import com.rajchenbergstudios.hoygenda.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first

const val TAG = "TTasksListFragment"

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TTasksListFragment : Fragment(R.layout.fragment_child_t_tasks_list),
    TTasksListAdapter.OnItemClickListener {

    private val viewModel: TTasksListViewModel by viewModels()
    private lateinit var searchView: SearchView
    private lateinit var menuHost: MenuHost

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadMenu()
        val binding = FragmentChildTTasksListBinding.bind(view)
        val tTasksListAdapter = TTasksListAdapter(this)

        binding.apply {

            tasksListRecyclerview.layoutTasksListRecyclerview.apply {
                adapter = tTasksListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = tTasksListAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTaskSwiped(task)
                }
            }).attachToRecyclerView(tasksListRecyclerview.layoutTasksListRecyclerview)
        }

        loadObservable(binding, tTasksListAdapter)
        loadTasksEventCollector()
    }

    private fun loadObservable(binding: FragmentChildTTasksListBinding, TTasksListAdapter: TTasksListAdapter) {
        viewModel.tasks.observe(viewLifecycleOwner){ tasksList ->
            binding.apply {
                HGDAViewStateUtils.apply {
                    if (tasksList.isEmpty()) {
                        setViewVisibility(tasksListRecyclerview.layoutTasksListRecyclerview, visibility = View.INVISIBLE)
                        setViewVisibility(tasksListLayoutNoData.layoutNoDataLinearlayout, visibility = View.VISIBLE)
                    } else {
                        setViewVisibility(tasksListRecyclerview.layoutTasksListRecyclerview, visibility = View.VISIBLE)
                        setViewVisibility(tasksListLayoutNoData.layoutNoDataLinearlayout, visibility = View.INVISIBLE)
                        TTasksListAdapter.submitList(tasksList)
                    }
                }
            }
        }
    }

    /**
     * TTasksListViewModel.TaskEvent.ShowUndoDeleteTaskMessage: Stays in this class. It asks for components relevant to this class.
     * TTasksListViewModel.TaskEvent.NavigateToEditTaskScreen: Stays in this class. The method it overrides comes from task list adapter.
     * TTasksListViewModel.TaskEvent.NavigateToDeleteAllCompletedScreen: Stays in this class. Relevant to menu which is in this class.
     * TTasksListViewModel.TaskEvent.NavigateToDeleteAllScreen: Stays in this class. Relevant to menu which is in this class.
     */
    private fun loadTasksEventCollector() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tasksEvent.collect { event ->
                when (event) {
                    is TTasksListViewModel.TaskEvent.ShowUndoDeleteTaskMessage -> {
                        Snackbar
                            .make(requireView(), "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO"){
                                viewModel.onUndoDeleteClick(event.task)
                            }
                            .show()
                    }
                    is TTasksListViewModel.TaskEvent.NavigateToEditTaskScreen -> {
                        val action = TodayFragmentDirections
                            .actionTodayFragmentToTaskAddEditFragment(task = event.task, title = "Edit task", taskinset = null, origin = 1)
                        findNavController().navigate(action)
                    }
                    is TTasksListViewModel.TaskEvent.NavigateToAddTaskToSetBottomSheet -> {
                        val action = TTasksListFragmentDirections.actionGlobalSetBottomSheetDialogFragment(task = event.task, origin = 1)
                        findNavController().navigate(action)
                    }
                    is TTasksListViewModel.TaskEvent.NavigateToDeleteAllCompletedScreen -> {
                        val action = TTasksListFragmentDirections
                            .actionGlobalTasksDeleteAllDialogFragment(origin = 1)
                        findNavController().navigate(action)
                    }
                    is TTasksListViewModel.TaskEvent.NavigateToDeleteAllScreen -> {
                        val action = TTasksListFragmentDirections
                            .actionGlobalTasksDeleteAllDialogFragment(origin = 3)
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

    inner class TasksMenuProvider : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menu.clear()
            menuInflater.inflate(R.menu.menu_t_tasks_list_fragment, menu)

            val searchItem = menu.findItem(R.id.pd_tasks_list_menu_search)
            searchView = searchItem.actionView as SearchView

            val pendingQuery = viewModel.searchQuery.value
            if (pendingQuery != null && pendingQuery.isNotEmpty()) {
                searchItem.expandActionView()
                searchView.setQuery(pendingQuery, false)
            }

            searchView.OnQueryTextChanged{ searchQuery ->
                viewModel.searchQuery.value = searchQuery
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                menu.findItem(R.id.tasks_list_menu_hide_completed).isChecked =
                    viewModel.preferencesFlow.first().hideCompleted
            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.pd_tasks_list_menu_sort_by_date -> {
                    viewModel.onSortOrderSelected(SortOrder.BY_TIME)
                    true
                }
                R.id.pd_tasks_list_menu_sort_alphabetically -> {
                    viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                    true
                }
                R.id.tasks_list_menu_hide_completed -> {
                    menuItem.isChecked = !menuItem.isChecked
                    viewModel.onHideCompletedSelected(menuItem.isChecked)
                    true
                }
                R.id.tasks_list_menu_delete_completed -> {
                    viewModel.onDeleteAllCompletedClick()
                    true
                }
                R.id.tasks_list_menu_delete_all -> {
                    viewModel.onDeleteAllClick()
                    true
                }
                else -> false
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }
}