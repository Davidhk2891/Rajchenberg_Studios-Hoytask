package com.rajchenbergstudios.hoytask.ui.todaytasktoset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rajchenbergstudios.hoytask.R
import com.rajchenbergstudios.hoytask.databinding.FragmentTodayTaskSaveToSetBinding
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



        return view
    }

    companion object {
        fun newInstance(): TaskToSetBottomSheetDialogFragment {
            return TaskToSetBottomSheetDialogFragment()
        }
    }
}