package com.rajchenbergstudios.hoytask.ui.daysdetails

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajchenbergstudios.hoytask.R
import com.rajchenbergstudios.hoytask.databinding.FragmentDaysDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DaysDetailsFragment : Fragment(R.layout.fragment_days_details) {

    private val viewModel: DaysDetailsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDaysDetailsBinding.bind(view)

        val daysDetailsTasksAdapter = DaysDetailsTasksAdapter()

        binding.apply {
            dayDetailsDateheader.apply {
                dateHeaderDayofmonth.text = viewModel.dayMonthDay
                dateHeaderMonth.text = viewModel.dayMonth
                dateHeaderYear.text = viewModel.dayYear
                dateHeaderDayofweek.text = viewModel.dayWeekDay
            }
            dayDetailsRecyclerview.apply {
                layoutTasksListRecyclerview.apply {
                    adapter = daysDetailsTasksAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                    setHasFixedSize(true)
                }
            }
        }
        //daysDetailsTasksAdapter.submitList(viewModel.tasks)
    }
}