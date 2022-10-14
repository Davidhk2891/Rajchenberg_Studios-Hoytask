package com.rajchenbergstudios.hoytask.ui.todaytasktoset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rajchenbergstudios.hoytask.R
import com.rajchenbergstudios.hoytask.databinding.FragmentTodayTaskSaveToSetBinding
import com.rajchenbergstudios.hoytask.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskToSetBottomSheetDialogFragment : BottomSheetDialogFragment(){

    private val viewModel: TaskToSetBottomSheetDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View = inflater.inflate(R.layout.fragment_today_task_save_to_set, container, false)

        val binding = FragmentTodayTaskSaveToSetBinding.bind(view)

        binding.apply {
            taskTodayAddToSetAddSetButton.setOnClickListener {
                viewModel.onCreateSetClicked()
            }
        }

        setFragmentResultListener("create_set_request"){_, bundle ->
            val result = bundle.getInt("create_set_result")
            viewModel.onCreateSetResult(result)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.taskToSetEvent.collect { event ->
                when (event) {
                    is TaskToSetBottomSheetDialogViewModel.TaskToSetEvent.NavigateToCreateTaskSetDialog -> {
                        val action = TaskToSetBottomSheetDialogFragmentDirections
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
                }.exhaustive
            }
        }

        return view
    }

    companion object {
        fun newInstance(): TaskToSetBottomSheetDialogFragment {
            return TaskToSetBottomSheetDialogFragment()
        }
    }
}