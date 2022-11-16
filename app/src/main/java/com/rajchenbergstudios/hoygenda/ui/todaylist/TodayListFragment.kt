package com.rajchenbergstudios.hoygenda.ui.todaylist

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
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.data.prefs.SortOrder
import com.rajchenbergstudios.hoygenda.data.today.Today
import com.rajchenbergstudios.hoygenda.databinding.FragmentTodayListBinding
import com.rajchenbergstudios.hoygenda.utils.OnQueryTextChanged
import com.rajchenbergstudios.hoygenda.utils.HGDAAnimationUtils
import com.rajchenbergstudios.hoygenda.utils.HGDAViewStateUtils
import com.rajchenbergstudios.hoygenda.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TodayListFragment : Fragment(R.layout.fragment_today_list), TodayListAdapter.OnItemClickListener{

    private val viewModel: TodayListViewModel by viewModels()

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

        val binding = FragmentTodayListBinding.bind(view)
        val todayListAdapter = TodayListAdapter(this)

        binding.apply {

            tasksListRecyclerview.layoutTasksListRecyclerview.apply {
                adapter = todayListAdapter
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
                    val task = todayListAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTaskSwiped(task)
                }
            }).attachToRecyclerView(tasksListRecyclerview.layoutTasksListRecyclerview)
        }

        todayDateDisplay(binding)
        initFabs(binding)
        loadObservable(binding, todayListAdapter)
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

    private fun loadObservable(binding: FragmentTodayListBinding, todayListAdapter: TodayListAdapter) {
        viewModel.tasks.observe(viewLifecycleOwner){ tasksList ->
            binding.apply {
                HGDAViewStateUtils.apply {
                    if (tasksList.isEmpty()) {
                        setViewVisibility(tasksListRecyclerview.layoutTasksListRecyclerview, visibility = View.INVISIBLE)
                        setViewVisibility(tasksListLayoutNoData.layoutNoDataLinearlayout, visibility = View.VISIBLE)
                    } else {
                        setViewVisibility(tasksListRecyclerview.layoutTasksListRecyclerview, visibility = View.VISIBLE)
                        setViewVisibility(tasksListLayoutNoData.layoutNoDataLinearlayout, visibility = View.INVISIBLE)
                        todayListAdapter.submitList(tasksList)
                    }
                }
            }
        }
    }

    private fun loadTasksEventCollector(binding: FragmentTodayListBinding) {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tasksEvent.collect { event ->
                when (event) {
                    is TodayListViewModel.TaskEvent.ShowUndoDeleteTaskMessage -> {
                        Snackbar
                            .make(requireView(), "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO"){
                                viewModel.onUndoDeleteClick(event.today)
                            }
                            .show()
                    }
                    is TodayListViewModel.TaskEvent.NavigateToAddTaskScreen -> {
                        val action = TodayListFragmentDirections
                            .actionTasksListFragmentToTaskAddEditFragment(task = null, title = "Add task", taskinset = null, origin = 1)
                        findNavController().navigate(action)
                    }
                    is TodayListViewModel.TaskEvent.NavigateToEditTaskScreen -> {
                        val action = TodayListFragmentDirections
                            .actionTasksListFragmentToTaskAddEditFragment(task = event.today, title = "Edit task", taskinset = null, origin = 1)
                        findNavController().navigate(action)
                    }
                    is TodayListViewModel.TaskEvent.NavigateToAddTaskToSetBottomSheet -> {
                        val action = TodayListFragmentDirections.actionGlobalSetBottomSheetDialogFragment(task = event.today, origin = 1)
                        findNavController().navigate(action)
                    }
                    is TodayListViewModel.TaskEvent.NavigateToAddTasksFromSetBottomSheet -> {
                        val action = TodayListFragmentDirections.actionGlobalSetBottomSheetDialogFragment(task = null, origin = 2)
                        findNavController().navigate(action)
                    }
                    is TodayListViewModel.TaskEvent.ShowTaskSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is TodayListViewModel.TaskEvent.NavigateToDeleteAllCompletedScreen -> {
                        val action = TodayListFragmentDirections
                            .actionGlobalTasksDeleteAllDialogFragment(origin = 1)
                        findNavController().navigate(action)
                    }
                    is TodayListViewModel.TaskEvent.NavigateToDeleteAllScreen -> {
                        val action = TodayListFragmentDirections
                            .actionGlobalTasksDeleteAllDialogFragment(origin = 3)
                        findNavController().navigate(action)
                    }
                    is TodayListViewModel.TaskEvent.ShowTaskSavedInNewOrOldSetConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg.toString(), Snackbar.LENGTH_LONG).show()
                    }
                    is TodayListViewModel.TaskEvent.ShowTaskAddedFromSetConfirmationMessage -> {
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

    private fun todayDateDisplay(binding: FragmentTodayListBinding) {
        binding.apply {
            tasksListDateheader.apply {
                dateHeaderDayofmonth.text = viewModel.getCurrentDayOfMonth()
                dateHeaderMonth.text =  viewModel.getCurrentMonth()
                dateHeaderYear.text = viewModel.getCurrentYear()
                dateHeaderDayofweek.text = viewModel.getCurrentDayOfWeek()
            }
        }
    }

    private fun initFabs(binding: FragmentTodayListBinding) {
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

    private fun onMainFabClick(binding: FragmentTodayListBinding) {
        setFabAnimationsAndViewStates(binding)
    }

    private fun setFabAnimationsAndViewStates(binding: FragmentTodayListBinding) {
        setFabAnimationVisibilityAndClickability(binding, fabClicked)
        fabClicked = !fabClicked
    }

    private fun setFabAnimationVisibilityAndClickability(binding: FragmentTodayListBinding, clicked: Boolean) {
        if (!clicked) fabAnimationsRollIn(binding) else fabAnimationsRollBack(binding)
    }

    private fun fabAnimationsRollIn(binding: FragmentTodayListBinding) {
        binding.apply {
            HGDAAnimationUtils.apply {
                HGDAViewStateUtils.apply {
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

    private fun fabAnimationsRollBack(binding: FragmentTodayListBinding) {
        binding.apply {
            HGDAAnimationUtils.apply {
                HGDAViewStateUtils.apply {
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

    override fun onItemClick(today: Today) {
        viewModel.onTaskSelected(today)
    }

    override fun onItemLongClick(today: Today) {
        viewModel.onTaskLongSelected(today)
    }

    override fun onCheckboxClick(today: Today, isChecked: Boolean) {
        viewModel.onTaskCheckedChanged(today, isChecked)
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