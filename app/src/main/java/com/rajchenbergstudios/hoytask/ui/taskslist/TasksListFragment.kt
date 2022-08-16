package com.rajchenbergstudios.hoytask.ui.taskslist

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rajchenbergstudios.hoytask.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksListFragment : Fragment(R.layout.fragment_tasks_list){

    private val viewModel: TasksListViewModel by viewModels()
}