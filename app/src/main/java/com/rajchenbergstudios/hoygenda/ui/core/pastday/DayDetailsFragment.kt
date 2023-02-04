package com.rajchenbergstudios.hoygenda.ui.core.pastday

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.databinding.FragmentDaysDetailsBinding
import com.rajchenbergstudios.hoygenda.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DaysDetailsFragment : Fragment(R.layout.fragment_days_details) {

    private val viewModel: DayDetailsViewModel by viewModels()

    private lateinit var viewPager: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDaysDetailsBinding.bind(view)
        todayDateDisplay(binding)
        initViewPagerWithTabLayout(binding)
    }

    private fun todayDateDisplay(binding: FragmentDaysDetailsBinding) {
        binding.apply {
            dayDetailsDateheader.apply {
                dateHeaderDayofmonth.text = viewModel.dayMonthDay
                dateHeaderMonth.text = viewModel.dayMonth
                dateHeaderYear.text = viewModel.dayYear
                dateHeaderDayofweek.text = viewModel.dayWeekDay
            }
        }
    }

    private fun initViewPagerWithTabLayout(binding: FragmentDaysDetailsBinding) {
        viewPager = binding.daysDetailsViewpager
        val tabLayout: TabLayout = binding.daysDetailsTablayout
        val dayDetailsPagerAdapter = activity?.let { DayDetailsPagerAdapter(it, viewModel.day) }
        viewPager.adapter = dayDetailsPagerAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, index ->
            tab.text = when (index) {
                0 -> "Tasks"
                1 -> "Journal"
                else -> throw Resources.NotFoundException("Tab not found at position")
            }.exhaustive
        }.attach()
    }
}