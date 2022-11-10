package com.rajchenbergstudios.hoytask.ui.activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rajchenbergstudios.hoytask.R
import com.rajchenbergstudios.hoytask.utils.HTSKViewStateUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

// private const val TAG = "MainActivity.kt"

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var bottomNavigationView: BottomNavigationView
    private val fadeIn: Animation by lazy { AnimationUtils.loadAnimation(baseContext, R.anim.fade_in) }
    private val fadeOut: Animation by lazy { AnimationUtils.loadAnimation(baseContext, R.anim.fade_out) }
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
        appBarConfiguration = AppBarConfiguration(setOf(R.id.tasksListFragment, R.id.daysListFragment, R.id.taskSetsListFragment))
        bottomNavigationView.setupWithNavController(navController)
    }

    private fun setupBottomNavStateConfig(){
        navController.addOnDestinationChangedListener{ _, destination, _ ->
            when (destination.id) {
                R.id.tasksListFragment -> HTSKViewStateUtils.setViewVisibility(v1 = bottomNavigationView, visibility = View.VISIBLE)
                R.id.taskSetsListFragment -> HTSKViewStateUtils.setViewVisibility(v1 = bottomNavigationView, visibility = View.VISIBLE)
                R.id.daysListFragment -> HTSKViewStateUtils.setViewVisibility(v1 = bottomNavigationView, visibility = View.VISIBLE)
                R.id.taskAddEditFragment -> HTSKViewStateUtils.setViewVisibility(v1 = bottomNavigationView, visibility = View.GONE)
                R.id.tasksSetEditListFragment -> HTSKViewStateUtils.setViewVisibility(v1 = bottomNavigationView, visibility = View.GONE)
                R.id.daysDetailsFragment -> HTSKViewStateUtils.setViewVisibility(v1 = bottomNavigationView, visibility = View.GONE)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    // Logger.i(TAG, "onTodayFabClicked", "Today Fab clicked")
}

const val ADD_TASK_RESULT_OK = Activity.RESULT_FIRST_USER
const val EDIT_TASK_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val CREATE_SET_RESULT_OK = Activity.RESULT_FIRST_USER + 2
const val EDIT_SET_RESULT_OK = Activity.RESULT_FIRST_USER + 3
const val ADD_TASK_FROM_SET_RESULT_OK = Activity.RESULT_FIRST_USER + 4

// TODO: Write down what happened in notebook (how you solved the expandable fab issue. That little attribute)