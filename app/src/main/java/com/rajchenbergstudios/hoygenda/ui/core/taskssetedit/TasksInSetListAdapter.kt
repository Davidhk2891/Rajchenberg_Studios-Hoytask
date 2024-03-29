package com.rajchenbergstudios.hoygenda.ui.core.taskssetedit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajchenbergstudios.hoygenda.data.taskinset.TaskInSet
import com.rajchenbergstudios.hoygenda.databinding.SingleItemTaskBinding

class TasksInSetListAdapter(private val listener: OnItemClickListener) : ListAdapter<TaskInSet, TasksInSetListAdapter.TaskInSetEditListViewHolder>(
    DiffCallback()
){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskInSetEditListViewHolder {

        val binding = SingleItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskInSetEditListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskInSetEditListViewHolder, position: Int) {

        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class TaskInSetEditListViewHolder(private val binding: SingleItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

        init {

            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val taskInSet = getItem(position)
                        listener.onItemClick(taskInSet)
                    }
                }
            }
        }

        fun bind(taskInSet: TaskInSet) {

            binding.apply {
                itemTaskCompletedCheckbox.isChecked = false
                itemTaskCompletedCheckbox.isClickable = false
                itemTaskTitleTextview.text = taskInSet.taskInSet
                itemTaskImportantImageview.visibility = View.INVISIBLE
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(taskInSet: TaskInSet)
    }

    class DiffCallback : DiffUtil.ItemCallback<TaskInSet>() {

        override fun areItemsTheSame(oldItem: TaskInSet, newItem: TaskInSet): Boolean
            = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TaskInSet, newItem: TaskInSet): Boolean
            = oldItem == newItem
    }
}