package com.rajchenbergstudios.hoygenda.ui.todaylist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajchenbergstudios.hoygenda.data.today.Today
import com.rajchenbergstudios.hoygenda.databinding.SingleItemTodayBinding

class TodayListAdapter(private val listener: OnItemClickListener) : ListAdapter<Today, TodayListAdapter.TasksListViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksListViewHolder {

        val binding = SingleItemTodayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TasksListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TasksListViewHolder, position: Int) {

        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class TasksListViewHolder(private val binding: SingleItemTodayBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        listener.onItemClick(task)
                    }
                }
                root.setOnLongClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        listener.onItemLongClick(task)
                    }
                    return@setOnLongClickListener true
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

        fun bind(today: Today){

            binding.apply {
                itemTaskCompletedCheckbox.isChecked = today.completed
                itemTaskTitleTextview.text = today.content
                itemTaskTitleTextview.paint.isStrikeThruText = today.completed
                itemTaskImportantImageview.isVisible = today.important
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(today: Today)
        fun onItemLongClick(today: Today)
        fun onCheckboxClick(today: Today, isChecked: Boolean)
    }

    class DiffCallback : DiffUtil.ItemCallback<Today>() {

        override fun areItemsTheSame(oldItem: Today, newItem: Today): Boolean
            = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Today, newItem: Today): Boolean
            = oldItem == newItem
    }
}