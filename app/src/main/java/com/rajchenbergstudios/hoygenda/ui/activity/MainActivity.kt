package com.rajchenbergstudios.hoygenda.ui.activity

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.navigation.NavigationView
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.ui.dayshistory.DaysListFragment
import com.rajchenbergstudios.hoygenda.ui.tasksset.TaskSetsListFragment
import com.rajchenbergstudios.hoygenda.ui.todaylists.TodayFragment
import com.rajchenbergstudios.hoygenda.utils.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


private const val TAG = "MainActivity.kt"

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView
    private var toolBar:Toolbar? = null

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {setKeepOnScreenCondition{viewModel.isLoading.value}}
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupCustomActionBar()
        setupNavControllerWithNavHostFrag()
        setupNavigationDrawer()

//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction().replace(R.id.hoytask_nav_host_fragment_container, TodayFragment()).commit()
//            navigationView.setCheckedItem(R.id.todayFragment)
//        }
    }

    private fun setupCustomActionBar(){
        toolBar = findViewById(R.id.hoytask_appbar)
        setSupportActionBar(toolBar)
    }

    private fun setupNavControllerWithNavHostFrag(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.hoytask_nav_host_fragment_container)
                as NavHostFragment
        navController = navHostFragment.findNavController()
        setupActionBarWithNavController(navController)
    }

    private fun setupNavigationDrawer(){
        drawerLayout = findViewById(R.id.hoytask_drawer_layout)
        navigationView = findViewById(R.id.hoytask_navigationview)
        navigationView.setNavigationItemSelectedListener(this)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null
        when (item.itemId) {
            R.id.todayFragment -> fragment = TodayFragment()
            R.id.taskSetsListFragment -> {
                fragment = TaskSetsListFragment()
                Logger.i(TAG, "onNavigationItemSelect", "Tasks Sets Frag selected")
            }
            R.id.daysListFragment -> fragment = DaysListFragment()
        }
        if (fragment != null) {
            supportFragmentManager.beginTransaction().replace(R.id.hoytask_nav_host_fragment_container, fragment).commit()
        }
        drawerLayout.closeDrawer(GravityCompat.START, true)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }
}
const val ADD_TASK_RESULT_OK = Activity.RESULT_FIRST_USER
const val EDIT_TASK_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val CREATE_SET_RESULT_OK = Activity.RESULT_FIRST_USER + 2
const val EDIT_SET_RESULT_OK = Activity.RESULT_FIRST_USER + 3
const val ADD_TASK_FROM_SET_RESULT_OK = Activity.RESULT_FIRST_USER + 4