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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.data.prefs.SortOrder
import com.rajchenbergstudios.hoygenda.databinding.FragmentParentTodayBinding
import com.rajchenbergstudios.hoygenda.ui.todaylists.taskslist.TasksListFragment
import com.rajchenbergstudios.hoygenda.ui.todaylists.taskslist.TasksListViewModel
import com.rajchenbergstudios.hoygenda.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first

private const val TAG = "TodayFragment"

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TodayFragment : Fragment(R.layout.fragment_parent_today), TasksListFragment.OnActionTakenFromListFragment {

    private val todayViewModel: TodayViewModel by viewModels()
    private val tasksListViewModel: TasksListViewModel by viewModels()
    private lateinit var binding: FragmentParentTodayBinding
    private lateinit var tabLayout: TabLayout
    private val viewPager: ViewPager2? = null
    private var fabClicked: Boolean = false
    private lateinit var searchView: SearchView

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
        initViewPagerWithTabLayout()
        todayDateDisplay(binding)
        initFabs(binding)
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

                    tasksListViewModel.searchQuery.value = searchQuery
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
                        tasksListViewModel.onDeleteAllCompletedClick()
                        true
                    }
                    R.id.tasks_list_menu_delete_all -> {
                        tasksListViewModel.onDeleteAllClick()
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
                dateHeaderDayofmonth.text = todayViewModel.getCurrentDayOfMonth()
                dateHeaderMonth.text =  todayViewModel.getCurrentMonth()
                dateHeaderYear.text = todayViewModel.getCurrentYear()
                dateHeaderDayofweek.text = todayViewModel.getCurrentDayOfWeek()
            }
        }
    }

    private fun initViewPagerWithTabLayout() {
        viewPager?.adapter = activity?.let { TodayPagerAdapter(it) }
        if (viewPager != null) {
            Logger.i(TAG, "initViewPagerWithTabLayout", "viewPager is not null")
            TabLayoutMediator(tabLayout, viewPager) { tab, index ->
                tab.text = when (index) {
                    0 -> "Tasks"
                    1 -> "Journal"
                    else -> throw Resources.NotFoundException("Tab not found at position")
                }.exhaustive
            }.attach()
        } else {
            Logger.i(TAG, "initViewPagerWithTabLayout", "viewPager is null")// viewPager is null. Fix it
        }
    }

    private fun initFabs(binding: FragmentParentTodayBinding) {
        binding.apply {
            tasksListFab.setOnClickListener {
                onMainFabClick(binding)
            }
            tasksListSubFab1.setOnClickListener {
                tasksListViewModel.onAddTasksFromSetClick()
            }
            tasksListSubFab2.setOnClickListener {
                tasksListViewModel.onAddNewTaskClick()
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

    override fun onTaskAddedFromSetConfirmationMessage() {
        fabClicked = true
        setFabAnimationsAndViewStates(binding)
    }

    override fun onChildFragmentPaused(mainFabClicked: Boolean) {
        fabClicked = mainFabClicked
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }
}