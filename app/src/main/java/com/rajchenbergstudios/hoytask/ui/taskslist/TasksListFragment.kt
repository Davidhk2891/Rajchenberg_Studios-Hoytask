package com.rajchenbergstudios.hoytask.ui.taskslist

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
import com.rajchenbergstudios.hoytask.utils.OnQueryTextChanged
import com.rajchenbergstudios.hoytask.utils.HTSKAnimationUtils
import com.rajchenbergstudios.hoytask.utils.HTSKViewStateUtils
import com.rajchenbergstudios.hoytask.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TasksListFragment : Fragment(R.layout.fragment_tasks_list), TasksListAdapter.OnItemClickListener{

    private val viewModel: TasksListViewModel by viewModels()

    private lateinit var searchView: SearchView

    private var fabClicked: Boolean = false

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim) }
    private val fadeIn: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in) }
    private val fadeOut: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksListBinding.bind(view)
        val tasksListAdapter = TasksListAdapter(this)

        binding.apply {

            tasksListRecyclerview.layoutTasksListRecyclerview.apply {
                adapter = tasksListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            tasksListTransparentWhiteScreen.setOnClickListener {
                fabAnimationsRollBack(binding)
                fabClicked = !fabClicked
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

        todayDateDisplay(binding)
        initFabs(binding)
        loadObservable(binding, tasksListAdapter)
        loadTasksEventCollector(binding)
        loadMenu()
        getFragmentResultListeners()
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
                    R.id.tasks_list_menu_delete_all -> {
                        viewModel.onDeleteAllClick()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun loadObservable(binding: FragmentTasksListBinding, tasksListAdapter: TasksListAdapter) {
        viewModel.tasks.observe(viewLifecycleOwner){ tasksList ->
            binding.apply {
                HTSKViewStateUtils.apply {
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

    private fun loadTasksEventCollector(binding: FragmentTasksListBinding) {
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
                        fabClicked = true
                        setFabAnimationsAndViewStates(binding)
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

    private fun initFabs(binding: FragmentTasksListBinding) {
        binding.apply {
            tasksListFab.setOnClickListener {
                onMainFabClick(binding)
            }
            tasksListSubFab1.setOnClickListener {
                viewModel.onAddTasksFromSetClick()
            }
            tasksListSubFab2.setOnClickListener {
                viewModel.onAddNewTaskClick()
            }
        }
    }

    private fun onMainFabClick(binding: FragmentTasksListBinding) {
        setFabAnimationsAndViewStates(binding)
    }

    private fun setFabAnimationsAndViewStates(binding: FragmentTasksListBinding) {
        setFabAnimationVisibilityAndClickability(binding, fabClicked)
        fabClicked = !fabClicked
    }

    private fun setFabAnimationVisibilityAndClickability(binding: FragmentTasksListBinding, clicked: Boolean) {
        if (!clicked) fabAnimationsRollIn(binding) else fabAnimationsRollBack(binding)
    }

    private fun fabAnimationsRollIn(binding: FragmentTasksListBinding) {
        binding.apply {
            HTSKAnimationUtils.apply {
                HTSKViewStateUtils.apply {
                    setViewAnimation(v1 = tasksListFab, a = rotateOpen)
                    setViewAnimation(v1 = tasksListSubFab1, v2 = tasksListSubFab2, a = fromBottom)
                    setViewAnimation(v1 = tasksListSubFab1Tv, v2 = tasksListSubFab2Tv, a = fromBottom)
                    setViewAnimation(v1 = tasksListTransparentWhiteScreen, a = fadeIn)
                    setViewVisibility(tasksListSubFab1, tasksListSubFab2
                        , tasksListSubFab1Tv, tasksListSubFab2Tv, View.VISIBLE)
                    setViewVisibility(v1 = tasksListTransparentWhiteScreen, visibility = View.VISIBLE)
                    setViewClickState(v1 = tasksListSubFab1, v2 = tasksListSubFab2, clickable = true)
                    setViewClickState(v1 = tasksListTransparentWhiteScreen, clickable = true)
                }
            }
        }
    }

    private fun fabAnimationsRollBack(binding: FragmentTasksListBinding) {
        binding.apply {
            HTSKAnimationUtils.apply {
                HTSKViewStateUtils.apply {
                    setViewAnimation(v1 = tasksListFab, a = rotateClose)
                    setViewAnimation(v1 = tasksListSubFab1, v2 = tasksListSubFab2, a = toBottom)
                    setViewAnimation(v1 = tasksListSubFab1Tv, v2 = tasksListSubFab2Tv, a = toBottom)
                    setViewAnimation(v1 = tasksListTransparentWhiteScreen, a = fadeOut)
                    setViewVisibility(tasksListSubFab1, tasksListSubFab2
                        , tasksListSubFab1Tv, tasksListSubFab2Tv, View.INVISIBLE)
                    setViewVisibility(v1 = tasksListTransparentWhiteScreen, visibility = View.INVISIBLE)
                    setViewClickState(v1 = tasksListSubFab1, v2 = tasksListSubFab2, clickable = false)
                    setViewClickState(v1 = tasksListTransparentWhiteScreen, clickable = false)
                }
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

    override fun onPause() {
        super.onPause()
        fabClicked = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }
}