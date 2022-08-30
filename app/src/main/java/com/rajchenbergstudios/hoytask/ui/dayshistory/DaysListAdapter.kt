package com.rajchenbergstudios.hoytask.ui.dayshistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajchenbergstudios.hoytask.data.day.Day
import com.rajchenbergstudios.hoytask.databinding.SingleItemDaysListBinding

class DaysListAdapter : ListAdapter<Day, DaysListAdapter.DaysListViewHolder>(DiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysListViewHolder {
        val binding = SingleItemDaysListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DaysListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DaysListViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class DaysListViewHolder(private val binding: SingleItemDaysListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(day: Day) {
            binding.apply {

                // Date header
                itemDayTasksDateheader.apply {
                    dateHeaderDayofmonth.text = day.dayOfMonth
                    dateHeaderMonth.text = day.month
                    dateHeaderYear.text = day.year
                    dateHeaderDayofweek.text = day.dayOfWeek
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Day>() {

        override fun areItemsTheSame(oldItem: Day, newItem: Day): Boolean
            = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Day, newItem: Day): Boolean
            = oldItem == newItem
    }
}