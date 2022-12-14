package com.rajchenbergstudios.hoygenda.ui.todaylists

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rajchenbergstudios.hoygenda.ui.todaylists.journalentrieslist.JEntriesListFragment
import com.rajchenbergstudios.hoygenda.ui.todaylists.taskslist.TasksListFragment
import com.rajchenbergstudios.hoygenda.utils.exhaustive
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TodayPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> TasksListFragment()
            1 -> JEntriesListFragment()
            else -> throw Resources.NotFoundException("Fragment on position not found")
        }.exhaustive
    }
}