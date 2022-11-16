package com.rajchenbergstudios.hoygenda.ui.daysdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajchenbergstudios.hoygenda.data.today.Today
import com.rajchenbergstudios.hoygenda.databinding.SingleItemTodayBinding

class DaysDetailsTasksAdapter : ListAdapter<Today, DaysDetailsTasksAdapter.DaysDetailsTasksViewHolder>(DiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysDetailsTasksViewHolder {
        val binding = SingleItemTodayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DaysDetailsTasksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DaysDetailsTasksViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class DaysDetailsTasksViewHolder(private val binding: SingleItemTodayBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(today: Today) {
            binding.apply {
                itemTaskTitleTextview.text = today.content
                itemTaskCompletedCheckbox.isClickable = false
                itemTaskCompletedCheckbox.isChecked = today.completed
                itemTaskTitleTextview.paint.isStrikeThruText = today.completed
                itemTaskImportantImageview.isVisible = today.important
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Today>() {

        override fun areItemsTheSame(oldItem: Today, newItem: Today): Boolean
            = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Today, newItem: Today): Boolean
            = oldItem.id == newItem.id
    }
}