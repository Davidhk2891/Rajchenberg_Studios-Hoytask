package com.rajchenbergstudios.hoygenda.ui.core.todaytasktoset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.data.taskset.TaskSet
import com.rajchenbergstudios.hoygenda.databinding.FragmentSetBottomSheetBinding
import com.rajchenbergstudios.hoygenda.utils.HGDAAnimationUtils
import com.rajchenbergstudios.hoygenda.utils.HGDAViewStateUtils
import com.rajchenbergstudios.hoygenda.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint

@Suppress("IMPLICIT_CAST_TO_ANY")
@AndroidEntryPoint
class SetBottomSheetDialogFragment : BottomSheetDialogFragment(),
    SetBottomSheetDialogAdapter.OnItemClickListener {

    private val viewModel: SetBottomSheetDialogViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_set_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startShimmerView()
        val binding = FragmentSetBottomSheetBinding.bind(view)
        val bottomSheetDialogAdapter = SetBottomSheetDialogAdapter(this)

        binding.apply {

            if (viewModel.origin == 2) {
                taskTodayAddToSetTitleTextview.text = getString(R.string.select_set)
                taskTodayAddToSetAddSetButton.apply {
                    isClickable = false
                    isVisible = false
                }
            }

            taskTodayAddToSetSetsRecyclerview.layoutTasksListRecyclerview.apply {
                adapter = bottomSheetDialogAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            taskTodayAddToSetAddSetButton.setOnClickListener {
                viewModel.onCreateSetClicked()
            }

            taskTodayAddToSetDoneButton.setOnClickListener {
                viewModel.onDoneClicked()
            }
        }

        setFragmentResultListener("create_set_request"){_, bundle ->
            val result = bundle.getInt("create_set_result")
            viewModel.onCreateSetResult(result)
        }

        viewModel.taskSets.observe(viewLifecycleOwner) { taskSetsList ->
            binding.apply {
                HGDAViewStateUtils.apply {
                    if (taskSetsList.isEmpty()) {
                        stopShimmerView()
                        setViewVisibility(v1 = taskTodayAddToSetSetsRecyclerview.layoutTasksListRecyclerview, visibility = View.INVISIBLE)
                        setViewVisibility(v1 = taskTodayAddToSetNoData.layoutNoDataLinearlayout, visibility = View.VISIBLE)
                        setViewVisibility(v1 = taskTodayAddToSetNoData.layoutNoDataImageview, visibility = View.GONE)
                        taskTodayAddToSetNoData.layoutNoDataTextview.text = getString(R.string.you_don_t_have_any_sets)
                    } else {
                        stopShimmerView()
                        setViewVisibility(v1 = taskTodayAddToSetSetsRecyclerview.layoutTasksListRecyclerview, visibility = View.VISIBLE)
                        setViewVisibility(v1 = taskTodayAddToSetNoData.layoutNoDataLinearlayout, visibility = View.INVISIBLE)
                        bottomSheetDialogAdapter.submitList(taskSetsList)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.taskToSetEvent.collect { event ->
                when (event) {
                    is SetBottomSheetDialogViewModel.TaskToSetEvent.NavigateToCreateTaskSetDialog -> {
                        val action = SetBottomSheetDialogFragmentDirections
                            .actionGlobalCreateTaskSetDialogFragment(task = event.task, origin = 2)
                        findNavController().navigate(action)
                    }
                    is SetBottomSheetDialogViewModel.TaskToSetEvent.NavigateBackWithResultFromSetCreatedWithTask -> {
                        setFragmentResult(
                            "create_set_request_2",
                            bundleOf("create_set_result_2" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is SetBottomSheetDialogViewModel.TaskToSetEvent.NavigateBackWithNoSetsSelected -> {
                        findNavController().popBackStack()
                    }
                    is SetBottomSheetDialogViewModel.TaskToSetEvent.NaigateBackWithResultUponSavingTaskToSet -> {
                        setFragmentResult(
                            "task_added_to_set_request",
                            bundleOf(
                                "task_added_to_set_result" to event.result,
                                "task_added_to_set_message" to event.msg)
                        )
                        findNavController().popBackStack()
                    }
                    is SetBottomSheetDialogViewModel.TaskToSetEvent.NavigateBackWithResultUponAddingTasksFromSet -> {
                        setFragmentResult(
                            "task_added_from_set_request",
                            bundleOf(
                                "task_added_from_set_result" to event.result,
                                "task_added_from_set_message" to event.msg
                            )
                        )
                        findNavController().popBackStack()
                    }
                }.exhaustive
            }
        }
    }

    private fun startShimmerView() { HGDAAnimationUtils.startShimmerView(requireDialog(), R.id.task_today_add_to_set_shimmerframelayout) }

    private fun stopShimmerView() { HGDAAnimationUtils.stopShimmerView(requireDialog(), R.id.task_today_add_to_set_shimmerframelayout) }

    override fun onTaskSetClick(taskSet: TaskSet, isChecked: Boolean) {
        viewModel.holdDataToSave(taskSet, isChecked)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onClearChosenStatus()
    }
}