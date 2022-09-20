package com.rajchenbergstudios.hoytask.ui.dayshistory

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajchenbergstudios.hoytask.R
import com.rajchenbergstudios.hoytask.data.day.Day
import com.rajchenbergstudios.hoytask.databinding.FragmentDaysHistoryBinding
import com.rajchenbergstudios.hoytask.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DaysListFragment : Fragment(R.layout.fragment_days_history), DaysListAdapter.OnItemClickListener {

    private val viewModel: DaysListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDaysHistoryBinding.bind(view)
        val daysListAdapter = DaysListAdapter(this)

        binding.apply {
            daysListRecyclerview.apply {
                adapter = daysListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.days.observe(viewLifecycleOwner){ daysList ->
            if (daysList.isEmpty()) {
                binding.daysListRecyclerview.visibility = View.INVISIBLE
                binding.daysListLayoutNoData.layoutNoDataLinearlayout.visibility = View.VISIBLE
                binding.daysListLayoutNoData.layoutNoDataTextview.text = getString(R.string.you_don_t_have_any_days)
                binding.daysListLayoutNoData.layoutNoDataImageview.visibility = View.GONE
            } else {
                binding.daysListRecyclerview.visibility = View.VISIBLE
                binding.daysListLayoutNoData.layoutNoDataLinearlayout.visibility = View.INVISIBLE
                daysListAdapter.submitList(daysList)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.daysEvent.collect { event ->
                when (event) {
                    is DaysListViewModel.DaysEvent.NavigateToDaysDetailsScreen -> {
                        val action = DaysListFragmentDirections.actionDaysListFragmentToDaysDetailsFragment(day = event.day)
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }
    }

    override fun onItemClick(day: Day) {
        viewModel.onDaySelected(day)
    }
}