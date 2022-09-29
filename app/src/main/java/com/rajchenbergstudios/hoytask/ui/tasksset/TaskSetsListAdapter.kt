package com.rajchenbergstudios.hoytask.ui.tasksset

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajchenbergstudios.hoytask.data.taskset.TaskSet
import com.rajchenbergstudios.hoytask.databinding.SingleItemTasksSetBinding

class TaskSetsListAdapter(private val listener: OnItemClickListener) : ListAdapter<TaskSet, TaskSetsListAdapter.TaskSetsListViewHolder>(DiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskSetsListViewHolder {

        val binding = SingleItemTasksSetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskSetsListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskSetsListViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class TaskSetsListViewHolder(private val binding: SingleItemTasksSetBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val taskSet = getItem(position)
                        listener.onItemClick(taskSet)
                    }
                }
            }
        }

        fun bind(set: TaskSet) {
            binding.apply {
                itemSetTitle.text = set.title
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(taskSet: TaskSet)
    }

    class DiffCallback : DiffUtil.ItemCallback<TaskSet>() {
        override fun areItemsTheSame(oldItem: TaskSet, newItem: TaskSet): Boolean
            = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TaskSet, newItem: TaskSet): Boolean
            = oldItem == newItem
    }
}