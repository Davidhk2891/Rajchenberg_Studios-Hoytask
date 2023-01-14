package com.rajchenbergstudios.hoygenda.ui.core.pastday.daydetailstaskslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajchenbergstudios.hoygenda.data.today.task.Task
import com.rajchenbergstudios.hoygenda.databinding.SingleItemTaskBinding

class PDTasksListAdapter(private val listener: OnItemClickListener) : ListAdapter<Task, PDTasksListAdapter.PDTasksListViewHolder>(
    DiffCallback()
){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PDTasksListViewHolder {
        val binding = SingleItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PDTasksListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PDTasksListViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class PDTasksListViewHolder(private val binding: SingleItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val pdTask = getItem(position)
                        listener.onItemClick(pdTask)
                    }
                }
            }
        }

        fun bind(task: Task) {
            binding.apply {
                itemTaskTitleTextview.text = task.title
                itemTaskCompletedCheckbox.isClickable = false
                itemTaskCompletedCheckbox.isChecked = task.completed
                itemTaskTitleTextview.paint.isStrikeThruText = task.completed
                itemTaskImportantImageview.isVisible = task.important
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(task: Task)
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {

        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean
                = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean
                = oldItem.id == newItem.id
    }
}