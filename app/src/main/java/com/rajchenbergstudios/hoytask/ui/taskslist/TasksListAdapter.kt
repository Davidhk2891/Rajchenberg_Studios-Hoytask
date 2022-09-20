package com.rajchenbergstudios.hoytask.ui.taskslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajchenbergstudios.hoytask.data.task.Task
import com.rajchenbergstudios.hoytask.databinding.SingleItemTasksListBinding

class TasksListAdapter(private val listener: OnItemClickListener) : ListAdapter<Task, TasksListAdapter.TasksListViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksListViewHolder {

        val binding = SingleItemTasksListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TasksListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TasksListViewHolder, position: Int) {

        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class TasksListViewHolder(private val binding: SingleItemTasksListBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        listener.onItemClick(task)
                    }
                }
                itemTaskCompletedCheckbox.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        listener.onCheckboxClick(task, itemTaskCompletedCheckbox.isChecked)
                    }
                }
            }
        }

        fun bind(task: Task){

            binding.apply {
                itemTaskCompletedCheckbox.isChecked = task.completed
                itemTaskTitleTextview.text = task.name
                itemTaskTitleTextview.paint.isStrikeThruText = task.completed
                itemTaskImportantImageview.isVisible = task.important
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(task: Task)
        fun onCheckboxClick(task: Task, isChecked: Boolean)
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {

        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean
            = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean
            = oldItem == newItem
    }
}