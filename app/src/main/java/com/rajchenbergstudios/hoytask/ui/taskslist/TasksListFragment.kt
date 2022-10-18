package com.rajchenbergstudios.hoytask.ui.taskslist

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rajchenbergstudios.hoytask.R
import com.rajchenbergstudios.hoytask.data.prefs.SortOrder
import com.rajchenbergstudios.hoytask.data.task.Task
import com.rajchenbergstudios.hoytask.databinding.FragmentTasksListBinding
import com.rajchenbergstudios.hoytask.util.OnQueryTextChanged
import com.rajchenbergstudios.hoytask.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TasksListFragment : Fragment(R.layout.fragment_tasks_list), TasksListAdapter.OnItemClickListener{

    private val viewModel: TasksListViewModel by viewModels()

    private lateinit var searchView: SearchView

    /*
    Below is for testing only, after you confirm that everything
    works, try to move all this logic to the viewModel.
     */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksListBinding.bind(view)

        val tasksListAdapter = TasksListAdapter(this)

        binding.apply {

            todayDateDisplay(binding)

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

            tasksListFab.setOnClickListener {
                viewModel.onAddNewTaskClick()
            }
        }

        viewModel.tasks.observe(viewLifecycleOwner){ tasksList ->
            if (tasksList.isEmpty()) {
                binding.tasksListLayoutNoData.layoutNoDataLinearlayout.visibility = View.VISIBLE
                binding.tasksListRecyclerview.layoutTasksListRecyclerview.visibility = View.INVISIBLE
            } else {
                binding.tasksListLayoutNoData.layoutNoDataLinearlayout.visibility = View.INVISIBLE
                binding.tasksListRecyclerview.layoutTasksListRecyclerview.visibility = View.VISIBLE
                tasksListAdapter.submitList(tasksList)
            }
        }

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
                        val action = TasksListFragmentDirections.actionGlobalTaskToSetBottomSheetDialogFragment(task = event.task)
                        findNavController().navigate(action)
                    }
                    is TasksListViewModel.TaskEvent.ShowTaskSavedConfirmationMessage -> {
                        Snackbar
                            .make(requireView(), event.msg, Snackbar.LENGTH_LONG)
                            .show()
                    }
                    is TasksListViewModel.TaskEvent.NavigateToDeleteAllCompletedScreen -> {
                        val action = TasksListFragmentDirections
                            .actionGlobalTasksDeleteAllCompletedDialogFragment()
                        findNavController().navigate(action)
                    }
                    is TasksListViewModel.TaskEvent.ShowTaskSavedInNewOrOldSetConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }

        loadMenu()
        getFragmentResultListeners()
        onSetDaysSaving()
    }

    private fun loadMenu(){

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object: MenuProvider{

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

                menuInflater.inflate(R.menu.menu_tasks_list_fragment, menu)

                val searchItem = menu.findItem(R.id.tasks_list_menu_search)
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
                    R.id.tasks_list_menu_sort_by_date -> {
                        viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                        true
                    }
                    R.id.tasks_list_menu_sort_by_name -> {
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
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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
    }

    private fun onFragmentResult(result: Int, message: String? = ""){
        viewModel.onFragmentResult(result, message)
    }

    private fun todayDateDisplay(binding: FragmentTasksListBinding) {
        binding.apply {
            tasksListDateheader.apply {
                dateHeaderDayofmonth.text = viewModel.getCurrentDayOfMonth()
                dateHeaderMonth.text =  viewModel.getCurrentMonth()
                dateHeaderYear.text = viewModel.getCurrentYear()
                dateHeaderDayofweek.text = viewModel.getCurrentDayOfWeek()
            }
        }
    }

    private fun onSetDaysSaving() {
        viewModel.onSetDaySaving(requireContext())
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