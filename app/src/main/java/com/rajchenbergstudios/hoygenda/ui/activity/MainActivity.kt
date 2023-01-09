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
import com.rajchenbergstudios.hoygenda.ui.today.TodayFragmentDirections
import com.rajchenbergstudios.hoygenda.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

// private const val TAG = "MainActivity.kt"

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var toolBar:Toolbar? = null
    private lateinit var currentFragName: String

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

    private fun initializeObjects(){
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
        currentFragName = "TodayFragment"
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.taskSetsListFragment -> {
                    when (currentFragName) {
                        "TodayFragment" -> {
                            viewModel.onTaskSetsListFragmentClick()
                        }
                    }
                    closeDrawer()
                    currentFragName = "TaskSetsListFragment"
                }
                R.id.daysListFragment -> {
                    when (currentFragName) {
                        "TodayFragment" -> {
                            viewModel.onDaysListFragmentClick()
                        }
                    }
                    closeDrawer()
                    currentFragName = "DaysListFragment"
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

    override fun onSupportNavigateUp(): Boolean {
        if (currentFragName == "TaskSetsListFragment" || currentFragName == "DaysListFragment") {
            unlockDrawer()
            openDrawer()
        }
        currentFragName = "TodayFragment"
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    private fun openDrawer() { drawerLayout.openDrawer(GravityCompat.START, false) }

    private fun closeDrawer() { drawerLayout.closeDrawer(GravityCompat.START, false) }

    private fun lockDrawer() { drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED) }

    private fun unlockDrawer() { drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED) }
}

const val ADD_TASK_RESULT_OK = Activity.RESULT_FIRST_USER
const val EDIT_TASK_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val CREATE_SET_RESULT_OK = Activity.RESULT_FIRST_USER + 2
const val EDIT_SET_RESULT_OK = Activity.RESULT_FIRST_USER + 3
const val ADD_TASK_FROM_SET_RESULT_OK = Activity.RESULT_FIRST_USER + 4
const val ADD_JENTRY_RESULT_OK = Activity.RESULT_FIRST_USER + 5
const val EDIT_JENTRY_RESULT_OK = Activity.RESULT_FIRST_USER + 6