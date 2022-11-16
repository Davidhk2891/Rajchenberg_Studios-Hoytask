package com.rajchenbergstudios.hoygenda.ui.dayshistory

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.data.day.Day
import com.rajchenbergstudios.hoygenda.databinding.FragmentDaysHistoryBinding
import com.rajchenbergstudios.hoygenda.utils.HGDAAnimationUtils
import com.rajchenbergstudios.hoygenda.utils.HGDAViewStateUtils
import com.rajchenbergstudios.hoygenda.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DaysListFragment : Fragment(R.layout.fragment_days_history), DaysListAdapter.OnItemClickListener {

    private val viewModel: DaysListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startShimmerView()
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
            binding.apply {
                HGDAViewStateUtils.apply {
                    if (daysList.isEmpty()) {
                        stopShimmerView()
                        setViewVisibility(daysListRecyclerview, visibility = View.INVISIBLE)
                        setViewVisibility(daysListLayoutNoData.layoutNoDataLinearlayout, visibility = View.VISIBLE)
                        setViewVisibility(daysListLayoutNoData.layoutNoDataImageview, visibility = View.GONE)
                        daysListLayoutNoData.layoutNoDataTextview.text = getString(R.string.you_don_t_have_any_days)
                    } else {
                        stopShimmerView()
                        setViewVisibility(daysListRecyclerview, visibility = View.VISIBLE)
                        setViewVisibility(daysListLayoutNoData.layoutNoDataLinearlayout, visibility = View.INVISIBLE)
                        daysListAdapter.submitList(daysList)
                    }
                }
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

    private fun startShimmerView() { HGDAAnimationUtils.startShimmerView(requireActivity(), R.id.days_list_shimmerframelayout) }

    private fun stopShimmerView() { HGDAAnimationUtils.stopShimmerView(requireActivity(), R.id.days_list_shimmerframelayout) }

    override fun onItemClick(day: Day) {
        viewModel.onDaySelected(day)
    }
}