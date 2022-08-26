package com.rajchenbergstudios.hoytask.ui.tasksset

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.rajchenbergstudios.hoytask.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskSetsListFragment : Fragment(R.layout.fragment_tasks_set){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}