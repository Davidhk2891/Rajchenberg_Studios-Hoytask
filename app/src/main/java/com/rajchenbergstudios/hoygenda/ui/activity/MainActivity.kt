package com.rajchenbergstudios.hoygenda.ui.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.utils.HGDAViewStateUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

// private const val TAG = "MainActivity.kt"

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var bottomNavigationView: BottomNavigationView

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {setKeepOnScreenCondition{viewModel.isLoading.value}}
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupCustomActionBar()
        setupNavControllerWithNavHostFrag()
        setupBottomNavigationView()
        setupActionBarWithNavController(navController, appBarConfiguration)
        setupBottomNavStateConfig()
    }

    private fun setupCustomActionBar(){
        setSupportActionBar(findViewById(R.id.hoytask_appbar))
    }

    private fun setupNavControllerWithNavHostFrag(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.hoytask_nav_host_fragment_container)
                as NavHostFragment
        navController = navHostFragment.findNavController()
    }

    private fun setupBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.hoytask_bottom_nav)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.todayFragment, R.id.taskSetsListFragment, R.id.daysListFragment))
        bottomNavigationView.setupWithNavController(navController)
    }

    private fun setupBottomNavStateConfig(){
        navController.addOnDestinationChangedListener{ _, destination, _ ->
            when (destination.id) {
                R.id.todayFragment -> HGDAViewStateUtils.setViewVisibility(v1 = bottomNavigationView, visibility = View.VISIBLE)
                R.id.tasksListFragment -> HGDAViewStateUtils.setViewVisibility(v1 = bottomNavigationView, visibility = View.VISIBLE)
                R.id.jEntriesListFragment -> HGDAViewStateUtils.setViewVisibility(v1 = bottomNavigationView, visibility = View.VISIBLE)
                R.id.taskSetsListFragment -> HGDAViewStateUtils.setViewVisibility(v1 = bottomNavigationView, visibility = View.VISIBLE)
                R.id.daysListFragment -> HGDAViewStateUtils.setViewVisibility(v1 = bottomNavigationView, visibility = View.VISIBLE)
                R.id.taskAddEditFragment -> HGDAViewStateUtils.setViewVisibility(v1 = bottomNavigationView, visibility = View.GONE)
                R.id.tasksSetEditListFragment -> HGDAViewStateUtils.setViewVisibility(v1 = bottomNavigationView, visibility = View.GONE)
                R.id.daysDetailsFragment -> HGDAViewStateUtils.setViewVisibility(v1 = bottomNavigationView, visibility = View.GONE)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

const val ADD_TASK_RESULT_OK = Activity.RESULT_FIRST_USER
const val EDIT_TASK_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val CREATE_SET_RESULT_OK = Activity.RESULT_FIRST_USER + 2
const val EDIT_SET_RESULT_OK = Activity.RESULT_FIRST_USER + 3
const val ADD_TASK_FROM_SET_RESULT_OK = Activity.RESULT_FIRST_USER + 4

// TODO: Create new branch for Drawer test
// TODO: Build and test ToolBar menu with the fragmets
