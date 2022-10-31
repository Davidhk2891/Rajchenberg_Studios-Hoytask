package com.rajchenbergstudios.hoytask.ui.todaytasktoset

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
import com.rajchenbergstudios.hoytask.R
import com.rajchenbergstudios.hoytask.data.taskset.TaskSet
import com.rajchenbergstudios.hoytask.databinding.FragmentSetBottomSheetBinding
import com.rajchenbergstudios.hoytask.utils.HTSKViewStateUtils
import com.rajchenbergstudios.hoytask.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint

@Suppress("IMPLICIT_CAST_TO_ANY")
@AndroidEntryPoint
class SetBottomSheetDialogFragment : BottomSheetDialogFragment(), SetBottomSheetDialogAdapter.OnItemClickListener{

    private val viewModel: TaskToSetBottomSheetDialogViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_set_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                HTSKViewStateUtils.apply {
                    if (taskSetsList.isEmpty()) {
                        setViewVisibility(v1 = taskTodayAddToSetSetsRecyclerview.layoutTasksListRecyclerview, visibility = View.INVISIBLE)
                        setViewVisibility(v1 = taskTodayAddToSetNoData.layoutNoDataLinearlayout, visibility = View.VISIBLE)
                        setViewVisibility(v1 = taskTodayAddToSetNoData.layoutNoDataImageview, visibility = View.GONE)
                        taskTodayAddToSetNoData.layoutNoDataTextview.text = getString(R.string.you_don_t_have_any_sets)
                    } else {
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
                    is TaskToSetBottomSheetDialogViewModel.TaskToSetEvent.NavigateToCreateTaskSetDialog -> {
                        val action = SetBottomSheetDialogFragmentDirections
                            .actionGlobalCreateTaskSetDialogFragment(task = event.task, origin = 2)
                        findNavController().navigate(action)
                    }
                    is TaskToSetBottomSheetDialogViewModel.TaskToSetEvent.NavigateBackWithResultFromSetCreatedWithTask -> {
                        setFragmentResult(
                            "create_set_request_2",
                            bundleOf("create_set_result_2" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is TaskToSetBottomSheetDialogViewModel.TaskToSetEvent.NavigateBackWithNoSetsSelected -> {
                        findNavController().popBackStack()
                    }
                    is TaskToSetBottomSheetDialogViewModel.TaskToSetEvent.NaigateBackWithResultUponSavingTaskToSet -> {
                        setFragmentResult(
                            "task_added_to_set_request",
                            bundleOf(
                                "task_added_to_set_result" to event.result,
                                "task_added_to_set_message" to event.msg)
                        )
                        findNavController().popBackStack()
                    }
                    is TaskToSetBottomSheetDialogViewModel.TaskToSetEvent.NavigateBackWithResultUponAddingTasksFromSet -> {
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

    override fun onTaskSetClick(taskSet: TaskSet, isChecked: Boolean) {
        viewModel.holdDataToSave(taskSet, isChecked)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onClearChosenStatus()
    }

    companion object {
        fun newInstance(): SetBottomSheetDialogFragment {
            return SetBottomSheetDialogFragment()
        }
    }
}