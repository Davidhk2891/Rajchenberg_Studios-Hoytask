package com.rajchenbergstudios.hoygenda.ui.todaylists

import android.content.res.Resources
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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.data.prefs.SortOrder
import com.rajchenbergstudios.hoygenda.databinding.FragmentParentTodayBinding
import com.rajchenbergstudios.hoygenda.ui.todaylists.taskslist.TasksListFragment
import com.rajchenbergstudios.hoygenda.ui.todaylists.taskslist.TasksListFragmentDirections
import com.rajchenbergstudios.hoygenda.ui.todaylists.taskslist.TasksListViewModel
import com.rajchenbergstudios.hoygenda.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first

private const val TAG = "TodayFragment"

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TodayFragment : Fragment(R.layout.fragment_parent_today), TasksListFragment.TasksListFragListener {

    private val viewModel: TodayViewModel by viewModels()
    private val tasksListViewModel: TasksListViewModel by viewModels()
    private lateinit var binding: FragmentParentTodayBinding
    private var fabClicked: Boolean = false
    private lateinit var searchView: SearchView
    private lateinit var searchViewData: MutableLiveData<String>

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim) }
    private val fadeIn: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in) }
    private val fadeOut: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentParentTodayBinding.bind(view)
        binding.apply {
            tasksListTransparentWhiteScreen.setOnClickListener {
                fabAnimationsRollBack(binding)
                fabClicked = !fabClicked
            }
        }
        loadMenu()
        initViewPagerWithTabLayout(binding)
        todayDateDisplay(binding)
        initFabs(binding)
        loadTodayEventCollector()
        getFragmentResultListeners()
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

    private fun loadTodayEventCollector() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.todayEvent.collect { event ->
                when (event) {
                    is TodayViewModel.TodayEvent.NavigateToAddTaskScreen -> {
                        val action = TodayFragmentDirections
                            .actionTodayFragmentToTaskAddEditFragment(task = null, title = "Add task"
                                , taskinset = null, origin = 1)
                        findNavController().navigate(action)
                    }
                    is TodayViewModel.TodayEvent.ShowTaskSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is TodayViewModel.TodayEvent.ShowTaskSavedInNewOrOldSetConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg.toString(), Snackbar.LENGTH_LONG).show()
                    }
                    is TodayViewModel.TodayEvent.ShowTaskAddedFromSetConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg.toString(), Snackbar.LENGTH_LONG).show()
                        fabClicked = true
                        setFabAnimationsAndViewStates(binding)
                    }
                    is TodayViewModel.TodayEvent.NavigateToAddTasksFromSetBottomSheet -> {
                        val action = TasksListFragmentDirections
                            .actionGlobalSetBottomSheetDialogFragment(task = null, origin = 2)
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }
    }

    private fun loadMenu(){

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

                menuInflater.inflate(R.menu.menu_tasks_list_fragment, menu)

                val searchItem = menu.findItem(R.id.tasks_list_menu_search)
                searchView = searchItem.actionView as SearchView

                val pendingQuery = tasksListViewModel.searchQuery.value
                if (pendingQuery != null && pendingQuery.isNotEmpty()) {
                    searchItem.expandActionView()
                    searchView.setQuery(pendingQuery, false)
                }

                searchView.OnQueryTextChanged{ searchQuery ->
                    tasksListViewModel.searchQuery.value = searchQuery // TODO: Doesn't work
                }

                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    menu.findItem(R.id.tasks_list_menu_hide_completed).isChecked =
                        tasksListViewModel.preferencesFlow.first().hideCompleted
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.tasks_list_menu_sort_by_date -> {
                        tasksListViewModel.onSortOrderSelected(SortOrder.BY_DATE)
                        true
                    }
                    R.id.tasks_list_menu_sort_by_name -> {
                        tasksListViewModel.onSortOrderSelected(SortOrder.BY_NAME)
                        true
                    }
                    R.id.tasks_list_menu_hide_completed -> {
                        menuItem.isChecked = !menuItem.isChecked
                        tasksListViewModel.onHideCompletedSelected(menuItem.isChecked)
                        true
                    }
                    R.id.tasks_list_menu_delete_completed -> {
                        tasksListViewModel.onDeleteAllCompletedClick() // TODO: Doesn't work
                        true
                    }
                    R.id.tasks_list_menu_delete_all -> {
                        tasksListViewModel.onDeleteAllClick() // TODO: Doesn't work
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun todayDateDisplay(binding: FragmentParentTodayBinding) {
        binding.apply {
            tasksListDateheader.apply {
                dateHeaderDayofmonth.text = viewModel.getCurrentDayOfMonth()
                dateHeaderMonth.text =  viewModel.getCurrentMonth()
                dateHeaderYear.text = viewModel.getCurrentYear()
                dateHeaderDayofweek.text = viewModel.getCurrentDayOfWeek()
            }
        }
    }

    private fun initViewPagerWithTabLayout(binding: FragmentParentTodayBinding) {
        val tasksListFragment = TasksListFragment()
        tasksListFragment.setListener(this)
        val viewPager: ViewPager2 = binding.todayViewpager
        val tabLayout: TabLayout = binding.todayTablayout
        viewPager.adapter = activity?.let { TodayPagerAdapter(it) }
            Logger.i(TAG, "initViewPagerWithTabLayout", "viewPager is not null")
            TabLayoutMediator(tabLayout, viewPager) { tab, index ->
                tab.text = when (index) {
                    0 -> "Tasks"
                    1 -> "Journal"
                    else -> throw Resources.NotFoundException("Tab not found at position")
                }.exhaustive
                when (index) {
                    0 -> {

                    }
                    1 -> {
                        fabClicked = false
                    }
                }
            }.attach()
    }

    private fun initFabs(binding: FragmentParentTodayBinding) {
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

    private fun onMainFabClick(binding: FragmentParentTodayBinding) {
        setFabAnimationsAndViewStates(binding)
    }

    private fun setFabAnimationsAndViewStates(binding: FragmentParentTodayBinding) {
        setFabAnimationVisibilityAndClickability(binding, fabClicked)
        fabClicked = !fabClicked
    }

    private fun setFabAnimationVisibilityAndClickability(binding: FragmentParentTodayBinding, clicked: Boolean) {
        if (!clicked) fabAnimationsRollIn(binding) else fabAnimationsRollBack(binding)
    }

    private fun fabAnimationsRollIn(binding: FragmentParentTodayBinding) {
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

    private fun fabAnimationsRollBack(binding: FragmentParentTodayBinding) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }
}