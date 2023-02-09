package com.rajchenbergstudios.hoygenda.ui.core.pastday

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rajchenbergstudios.hoygenda.data.day.Day
import com.rajchenbergstudios.hoygenda.ui.core.pastday.daydetailsjentrieslist.PDJEntriesListFragment
import com.rajchenbergstudios.hoygenda.ui.core.pastday.daydetailstaskslist.PDTasksListFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class DayDetailsPagerAdapter(fragmentActivity: FragmentActivity, val day: Day?) : FragmentStateAdapter(fragmentActivity) {

    private lateinit var pdTasksListFragment: PDTasksListFragment
    private lateinit var pdJEntriesListFragment: PDJEntriesListFragment

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {
                pdTasksListFragment = PDTasksListFragment()

                val bundle = Bundle()
                bundle.putParcelable("day", day)
                pdTasksListFragment.arguments = bundle

                pdTasksListFragment
            }
            1 -> {
                pdJEntriesListFragment = PDJEntriesListFragment()

                val bundle = Bundle()
                bundle.putParcelable("day", day)
                pdJEntriesListFragment.arguments = bundle

                pdJEntriesListFragment
            }
            else -> throw Resources.NotFoundException("Fragment on position not found")
        }
    }
}