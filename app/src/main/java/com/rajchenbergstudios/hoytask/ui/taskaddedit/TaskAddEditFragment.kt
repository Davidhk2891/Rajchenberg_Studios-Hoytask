package com.rajchenbergstudios.hoytask.ui.taskaddedit

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rajchenbergstudios.hoytask.R
import com.rajchenbergstudios.hoytask.databinding.FragmentAddEditTaskBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskAddEditFragment : Fragment(R.layout.fragment_add_edit_task){

    private val viewModel: TaskAddEditViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditTaskBinding.bind(view)

        binding.apply {
            fragmentAddEditTitleEdittext.setText(viewModel.taskName)
            fragmentAddEditImportantCheckbox.isChecked = viewModel.taskImportance
            fragmentAddEditImportantCheckbox.jumpDrawablesToCurrentState()
            fragmentAddEditCreatedTextview.isVisible = viewModel.task != null
            val dateCreated = "Date created: ${viewModel.task?.createdDateFormat}"
            fragmentAddEditCreatedTextview.text = dateCreated
        }
    }
}