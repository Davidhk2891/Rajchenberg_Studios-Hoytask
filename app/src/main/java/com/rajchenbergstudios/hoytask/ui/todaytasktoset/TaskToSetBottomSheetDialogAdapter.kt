package com.rajchenbergstudios.hoytask.ui.todaytasktoset

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajchenbergstudios.hoytask.data.taskset.TaskSet
import com.rajchenbergstudios.hoytask.databinding.SingleItemTasksSetBinding

class TaskToSetBottomSheetDialogAdapter(private val listener: OnItemClickListener) : ListAdapter<TaskSet, TaskToSetBottomSheetDialogAdapter.TaskToSetBottomSheetDialogViewHolder>(DiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskToSetBottomSheetDialogViewHolder {

        val binding = SingleItemTasksSetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskToSetBottomSheetDialogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskToSetBottomSheetDialogViewHolder, position: Int) {

        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class TaskToSetBottomSheetDialogViewHolder(private val binding: SingleItemTasksSetBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val taskSet = getItem(position)
                        itemSetAddToCheckbox.isChecked = !itemSetAddToCheckbox.isChecked
                        listener.onTaskSetClick(taskSet, itemSetAddToCheckbox.isChecked)
                    }
                }
                itemSetAddToCheckbox.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val taskSet = getItem(position)
                        listener.onTaskSetClick(taskSet, itemSetAddToCheckbox.isChecked)
                    }
                }
            }
        }

        fun bind(taskSet: TaskSet) {
            binding.apply {
                itemSetTitle.text = taskSet.title
                itemSetAddToCheckbox.isChecked = taskSet.chosen
            }
        }
    }

    interface OnItemClickListener {
        fun onTaskSetClick(taskSet: TaskSet, isChecked: Boolean)
    }

    class DiffCallback : DiffUtil.ItemCallback<TaskSet>() {

        override fun areItemsTheSame(oldItem: TaskSet, newItem: TaskSet): Boolean
            = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TaskSet, newItem: TaskSet): Boolean
            = oldItem == newItem
    }
}