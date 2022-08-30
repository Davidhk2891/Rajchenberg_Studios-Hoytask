package com.rajchenbergstudios.hoytask.ui.dayshistory

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajchenbergstudios.hoytask.R
import com.rajchenbergstudios.hoytask.databinding.FragmentDaysHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DaysListFragment : Fragment(R.layout.fragment_days_history){

    private val viewModel: DaysListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDaysHistoryBinding.bind(view)
        val daysListAdapter = DaysListAdapter()

        binding.apply {
            daysListRecyclerview.apply {
                adapter = daysListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.days.observe(viewLifecycleOwner){ daysList ->
            daysListAdapter.submitList(daysList)
        }
    }
}