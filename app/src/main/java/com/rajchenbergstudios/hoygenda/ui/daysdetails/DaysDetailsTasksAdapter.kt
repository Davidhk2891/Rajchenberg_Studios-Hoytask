package com.rajchenbergstudios.hoygenda.ui.daysdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajchenbergstudios.hoygenda.data.today.task.Task
import com.rajchenbergstudios.hoygenda.databinding.SingleItemTaskBinding

class DaysDetailsTasksAdapter : ListAdapter<Task, DaysDetailsTasksAdapter.DaysDetailsTasksViewHolder>(DiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysDetailsTasksViewHolder {
        val binding = SingleItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DaysDetailsTasksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DaysDetailsTasksViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class DaysDetailsTasksViewHolder(private val binding: SingleItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

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

    class DiffCallback : DiffUtil.ItemCallback<Task>() {

        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean
            = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean
            = oldItem.id == newItem.id
    }
}