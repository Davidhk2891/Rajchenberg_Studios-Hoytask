package com.rajchenbergstudios.hoytask.ui.taskslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajchenbergstudios.hoytask.data.task.Task
import com.rajchenbergstudios.hoytask.databinding.ItemTasksListBinding

class TasksListAdapter : ListAdapter<Task, TasksListAdapter.TasksListViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksListViewHolder {

        val binding = ItemTasksListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TasksListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TasksListViewHolder, position: Int) {

        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class TasksListViewHolder(private val binding: ItemTasksListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task){

            binding.apply {
                itemTaskCompletedCheckbox.isChecked = task.completed
                itemTaskTitleTextview.text = task.name
                itemTaskTitleTextview.paint.isStrikeThruText = task.completed
                itemTaskImportantImageview.isVisible = task.important
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {

        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean
            = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean
            = oldItem == newItem
    }
}