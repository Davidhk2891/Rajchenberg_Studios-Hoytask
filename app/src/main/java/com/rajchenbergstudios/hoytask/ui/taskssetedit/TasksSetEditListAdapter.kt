package com.rajchenbergstudios.hoytask.ui.taskssetedit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajchenbergstudios.hoytask.data.taskset.TaskSet
import com.rajchenbergstudios.hoytask.databinding.SingleItemTasksListBinding

class TasksSetEditListAdapter : ListAdapter<TaskSet, TasksSetEditListAdapter.TasksSetEditListViewHolder>(DiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksSetEditListViewHolder {
        val binding = SingleItemTasksListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TasksSetEditListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TasksSetEditListViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class TasksSetEditListViewHolder(private val binding: SingleItemTasksListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(taskSet: TaskSet) {
            binding.apply {
                itemTaskCompletedCheckbox.isChecked = false
                itemTaskCompletedCheckbox.isClickable = false
                itemTaskTitleTextview.text = taskSet.title
                itemTaskImportantImageview.visibility = View.INVISIBLE
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TaskSet>() {

        override fun areItemsTheSame(oldItem: TaskSet, newItem: TaskSet): Boolean
            = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TaskSet, newItem: TaskSet): Boolean
            = oldItem == newItem
    }
}