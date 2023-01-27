package com.rajchenbergstudios.hoygenda.ui.core.createtaskset

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.databinding.FragmentTaskSetCreateBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateTaskSetDialogFragment : DialogFragment(){

    private val viewModel: CreateTaskSetDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View = inflater.inflate(R.layout.fragment_task_set_create, container, false)

        val binding = FragmentTaskSetCreateBinding.bind(view)

        binding.apply {
            taskSetCreateOptionsCreateButton.setOnClickListener {
                viewModel.onCreateTaskSetClick(taskSetCreateSetTitleEdittext.text.toString())
            }
            taskSetCreateOptionsCancelButton.setOnClickListener {
                viewModel.onCancelTaskSetClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.createTaskSetEvent.collect { event ->
                when (event) {
                    is CreateTaskSetDialogViewModel.CreateTaskSetEvent.ShowInvalidInputMessage -> {
                        Toast.makeText(requireContext(), event.msg, Toast.LENGTH_LONG).show()
                    }
                    is CreateTaskSetDialogViewModel.CreateTaskSetEvent.DoTaskSetCancellationAction -> {
                        dismiss()
                    }
                    is CreateTaskSetDialogViewModel.CreateTaskSetEvent.GoToSetAndShowSetCreatedConfirmationMessage -> {
                        Toast.makeText(requireContext(), event.msg, Toast.LENGTH_LONG).show()
                        val action = CreateTaskSetDialogFragmentDirections
                            .actionCreateTaskSetDialogFragmentToTasksSetEditListFragment(settitle = event.title)
                        findNavController().navigate(action)
                    }
                    is CreateTaskSetDialogViewModel.CreateTaskSetEvent.DoNotGoToSetAndShowSetCreatedConfirmationMessage -> {
                        dismiss()
                        setFragmentResult(
                            "create_set_request",
                            bundleOf("create_set_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                }
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val binding = FragmentTaskSetCreateBinding.bind(view)
//        focusEditText(binding.taskSetCreateSetTitleEdittext)
    }

    override fun onStart() {
        super.onStart()
        focusEditText()
    }

    private fun focusEditText(editText: EditText) {
//        val imm: InputMethodManager = dialog?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)

        // binding.taskSetCreateSetTitleEdittext.focusAndShowKeyboard(requireActivity())
    }

    private fun focusEditText() {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        // TODO: Carry on trying out solutions from Chat GPT. If come to a halt, check on Stackoverflow
    }
}