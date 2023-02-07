package com.rajchenbergstudios.hoygenda.ui.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.navigation.NavigationView
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.ui.core.today.TodayFragment
import com.rajchenbergstudios.hoygenda.ui.core.today.TodayFragmentDirections
import com.rajchenbergstudios.hoygenda.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

// private const val TAG = "MainActivity.kt"

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), TodayFragment.TodayFragmentListener {

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var toolBar:Toolbar? = null
    private var inOtherDestination: Boolean = false

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {setKeepOnScreenCondition{viewModel.isLoading.value}}
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeObjects()
        setupCustomActionBar()
        setupNavController()
        setupNavigationViewNavigation()
        setupNavigationDrawerListener()
        setupActionBarWithNavController(navController, drawerLayout)
        loadMainEventCollector()
    }

    private fun initializeObjects() {
        toolBar = findViewById(R.id.hoytask_appbar)
        drawerLayout = findViewById(R.id.hoytask_drawer_layout)
        navigationView = findViewById(R.id.hoytask_navigationview)
    }

    private fun setupCustomActionBar() {
        setSupportActionBar(toolBar)
    }

    private fun setupNavController() {
        navController = Navigation.findNavController(this, R.id.hoytask_nav_host_fragment_container)
        NavigationUI.setupWithNavController(navigationView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
    }

    private fun setupNavigationViewNavigation() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.todayFragment -> {
                    closeDrawer(true)
                }
                R.id.taskSetsListFragment -> {
                    viewModel.onTaskSetsListFragmentClick()
                    closeDrawer(false)
                    lockDrawer()
                    inOtherDestination = true
                }
                R.id.daysListFragment -> {
                    viewModel.onDaysListFragmentClick()
                    closeDrawer(false)
                    lockDrawer()
                    inOtherDestination = true
                }
                R.id.tutorialFragment -> {
                    viewModel.onTutorialRedirectionEngaged()
                    inOtherDestination = true
                }
                R.id.getInTouchDialogFragment -> {
                    viewModel.onGetInTouchDialogFragmentClick()
                    inOtherDestination = true
                }
                R.id.leaveReviewDialogFragment -> {
                    viewModel.onLeaveReviewDialogFragmentClick()
                    inOtherDestination = true
                }
                R.id.changelogDialogFragment -> {
                    viewModel.onChangelogDialogFragmentClick()
                    inOtherDestination = true
                }
                R.id.tellYourFriendsDialogFragment -> {
                    viewModel.onTellYourFriendsDialogFragmentClick()
                    inOtherDestination = true
                }
                R.id.aboutDialogFragment -> {
                    viewModel.onAboutDialogFragmentClick()
                    inOtherDestination = true
                }
            }
            true
        }
    }

    private fun loadMainEventCollector() {
        this.lifecycleScope.launchWhenStarted {
            viewModel.mainEvent.collect { mainEvent ->
                when (mainEvent) {
                    MainViewModel.MainEvent.NavigateToTaskSetsListFragment -> {
                        navController.navigate(TodayFragmentDirections.actionGlobalTaskSetsListFragment())
                        lockDrawer()
                    }
                    MainViewModel.MainEvent.NavigateToDaysListFragment -> {
                        navController.navigate(TodayFragmentDirections.actionGlobalDaysListFragment())
                        lockDrawer()
                    }
                    MainViewModel.MainEvent.NavigateToGetInTouchDialog -> {
                        navController.navigate(TodayFragmentDirections.actionGlobalGetInTouchDialogFragment())
                    }
                    MainViewModel.MainEvent.NavigateToLeaveReviewDialog -> {
                        navController.navigate(TodayFragmentDirections.actionGlobalLeaveReviewDialogFragment())
                    }
                    MainViewModel.MainEvent.NavigateToChangelogDialog -> {
                        navController.navigate(TodayFragmentDirections.actionGlobalChangelogDialogFragment())
                    }
                    MainViewModel.MainEvent.NavigateToTellYourFriendsDialog -> {
                        navController.navigate(TodayFragmentDirections.actionGlobalTellYourFriendsDialogFragment())
                    }
                    MainViewModel.MainEvent.NavigateToAboutDialog -> {
                        navController.navigate(TodayFragmentDirections.actionGlobalAboutDialogFragment())
                    }
                    MainViewModel.MainEvent.NavigateToTutorialFragment -> {
                        navController.navigate(TodayFragmentDirections.actionTodayFragmentToTutorialFragment())
                        closeDrawer(false)
                        lockDrawer()
                    }
                }.exhaustive
            }
        }
    }

    private fun setupNavigationDrawerListener() {
        drawerLayout.addDrawerListener(object: DrawerLayout.DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {

            }

            override fun onDrawerStateChanged(newState: Int) {

            }
        })
    }

    private fun openDrawer() { drawerLayout.openDrawer(GravityCompat.START, false) }

    private fun closeDrawer(animate: Boolean) { drawerLayout.closeDrawer(GravityCompat.START, animate) }

    private fun lockDrawer() { drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED) }

    private fun unlockDrawer() { drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED) }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    override fun todayOnResumeCalled() {
        if (inOtherDestination) {
            openDrawer()
            unlockDrawer()
            inOtherDestination = false
        }
    }
}

/*
Activity.RESULT_OK = -1
Activity.RESULT_CANCELED = 0
Activity.FIRST_USER = 1
 */

const val ADD_TASK_RESULT_OK = Activity.RESULT_FIRST_USER
const val EDIT_TASK_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val CREATE_SET_RESULT_OK = Activity.RESULT_FIRST_USER + 2
const val EDIT_SET_RESULT_OK = Activity.RESULT_FIRST_USER + 3
const val ADD_TASK_FROM_SET_RESULT_OK = Activity.RESULT_FIRST_USER + 4
const val ADD_JENTRY_RESULT_OK = Activity.RESULT_FIRST_USER + 5
const val EDIT_JENTRY_RESULT_OK = Activity.RESULT_FIRST_USER + 6
const val ADD_TASK_IN_SET_OK = Activity.RESULT_FIRST_USER + 7
const val EDIT_TASK_IN_SET_OK = Activity.RESULT_FIRST_USER + 8