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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.data.prefs.SortOrder
import com.rajchenbergstudios.hoygenda.databinding.FragmentParentTodayBinding
import com.rajchenbergstudios.hoygenda.ui.todaylists.taskslist.TasksListFragmentDirections
import com.rajchenbergstudios.hoygenda.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first

private const val TAG = "TodayFragment"

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TodayFragment : Fragment(R.layout.fragment_parent_today) {

    private val viewModel: TodayViewModel by viewModels()
    private lateinit var binding: FragmentParentTodayBinding
    private var fabClicked: Boolean = false

    private lateinit var viewPager: ViewPager2

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
        initViewPagerWithTabLayout(binding)
        todayDateDisplay(binding)
        initFabs(binding)
        loadTodayEventCollector()
        getFragmentResultListeners()
        loadMenu()
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

    /**
     * TodayViewModel.TodayEvent.NavigateToAddTaskScreen: Relevant to this class. Belongs to Fab which are all in this class.
     * TodayViewModel.TodayEvent.ShowTaskSavedConfirmationMessage: Relevant to this class. Belongs to onFragmentResultListener which is here.
     * TodayViewModel.TodayEvent.ShowTaskSavedInNewOrOldSetConfirmationMessage: Relevant to this class. Belongs to onFragmentResultListener which is here.
     * TodayViewModel.TodayEvent.ShowTaskAddedFromSetConfirmationMessage: Relevant to this class. Belongs to onFragmentResultListener which is here.
     * TodayViewModel.TodayEvent.NavigateToAddTasksFromSetBottomSheet: Relevant to this class. Belongs to Fab which are all in this class.
     */
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
                        setViewPagerPage(0)
                    }
                    is TodayViewModel.TodayEvent.ShowTaskSavedInNewOrOldSetConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg.toString(), Snackbar.LENGTH_LONG).show()
                    }
                    is TodayViewModel.TodayEvent.ShowTaskAddedFromSetConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg.toString(), Snackbar.LENGTH_LONG).show()
                        fabClicked = true
                        setFabAnimationsAndViewStates(binding)
                        setViewPagerPage(0)
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

    // This will soon be used to be 1
    private fun setViewPagerPage(index: Int){
        viewModel.postActionWithDelay(300, object: TodayViewModel.PostActionListener{
            override fun onDelayFinished() {
                viewPager.setCurrentItem(index, true)
            }
        })
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
        viewPager = binding.todayViewpager
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
                Logger.i(TAG, "initFabs", "Coming soon")
            }
            tasksListSubFab2.setOnClickListener {
                viewModel.onAddTasksFromSetClick()
            }
            tasksListSubFab3.setOnClickListener {
                viewModel.onAddNewTaskClick()
            }
        }
    }

    private fun loadMenu(){
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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
                    setViewAnimation(v1 = tasksListSubFab1, v2 = tasksListSubFab2, v3 = tasksListSubFab3, a = fromBottom)
                    setViewAnimation(v1 = tasksListSubFab1Tv, v2 = tasksListSubFab2Tv, v3 = tasksListSubFab3Tv, a = fromBottom)
                    setViewAnimation(v1 = tasksListTransparentWhiteScreen, a = fadeIn)
                    setViewVisibility(v1 = tasksListSubFab1, v2 = tasksListSubFab2, v3 = tasksListSubFab3
                        , v4 = tasksListSubFab1Tv, v5 = tasksListSubFab2Tv, v6 = tasksListSubFab3Tv, visibility = View.VISIBLE)
                    setViewVisibility(v1 = tasksListTransparentWhiteScreen, visibility = View.VISIBLE)
                    setViewClickState(v1 = tasksListSubFab1, v2 = tasksListSubFab2, v3 = tasksListSubFab3, clickable = true)
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
                    setViewAnimation(v1 = tasksListSubFab1, v2 = tasksListSubFab2, v3 = tasksListSubFab3, a = toBottom)
                    setViewAnimation(v1 = tasksListSubFab1Tv, v2 = tasksListSubFab2Tv, v3 = tasksListSubFab3Tv, a = toBottom)
                    setViewAnimation(v1 = tasksListTransparentWhiteScreen, a = fadeOut)
                    setViewVisibility(v1 = tasksListSubFab1, v2 = tasksListSubFab2, v3 = tasksListSubFab3
                        , v4 = tasksListSubFab1Tv, v5 = tasksListSubFab2Tv, v6 = tasksListSubFab3Tv, visibility = View.INVISIBLE)
                    setViewVisibility(v1 = tasksListTransparentWhiteScreen, visibility = View.INVISIBLE)
                    setViewClickState(v1 = tasksListSubFab1, v2 = tasksListSubFab2, v3 = tasksListSubFab3, clickable = false)
                    setViewClickState(v1 = tasksListTransparentWhiteScreen, clickable = false)
                }
            }
        }
    }
}