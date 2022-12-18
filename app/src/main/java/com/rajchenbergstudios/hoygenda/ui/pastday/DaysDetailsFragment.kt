package com.rajchenbergstudios.hoygenda.ui.pastday

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.databinding.FragmentDaysDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            withContext(Dispatchers.IO) {
                daysDetailsTasksAdapter.submitList(viewModel.mTasks)
            }
        }
    }
}