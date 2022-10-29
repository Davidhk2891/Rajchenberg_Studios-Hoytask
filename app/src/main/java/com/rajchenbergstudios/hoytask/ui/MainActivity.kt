package com.rajchenbergstudios.hoytask.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rajchenbergstudios.hoytask.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadSplashScreen()
        setupCustomActionBar()
        setupNavControllerWithNavHostFrag()
        setupBottomNavigationView()
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupCustomActionBar(){
        setSupportActionBar(findViewById(R.id.hoytask_appbar))
    }

    private fun setupNavControllerWithNavHostFrag(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
                as NavHostFragment
        navController = navHostFragment.findNavController()
    }

    private fun setupBottomNavigationView(){
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.hoytask_bottom_nav)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.tasksListFragment, R.id.daysListFragment, R.id.taskSetsListFragment))
        bottomNavigationView.setupWithNavController(navController)
    }

    private fun loadSplashScreen(){
        installSplashScreen()
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

// TODO: Write down what happened in notebook (how you solved the expandable fab issue. That little attribute)