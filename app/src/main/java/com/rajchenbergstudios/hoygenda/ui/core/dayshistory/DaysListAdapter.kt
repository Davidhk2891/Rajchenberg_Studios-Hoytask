package com.rajchenbergstudios.hoygenda.ui.core.dayshistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajchenbergstudios.hoygenda.data.day.Day
import com.rajchenbergstudios.hoygenda.databinding.SingleItemDaysListBinding

class DaysListAdapter(private val listener: OnItemClickListener) : ListAdapter<Day, DaysListAdapter.DaysListViewHolder>(
    DiffCallback()
){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysListViewHolder {
        val binding = SingleItemDaysListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DaysListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DaysListViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class DaysListViewHolder(private val binding: SingleItemDaysListBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val day = getItem(position)
                        val formattedDay = Day(
                            day.dayOfWeek,
                            day.formattedDayOfMonth(),
                            day.month,
                            day.year,
                            day.listOfTasks,
                            day.listOfJEntries,
                            day.id)
                        listener.onItemClick(formattedDay)
                    }
                }
            }
        }

        fun bind(day: Day) {
            binding.apply {

                // Date header
                itemDayTasksDateheader.apply {

                    dateHeaderDayofmonth.text = day.formattedDayOfMonth()
                    dateHeaderMonth.text = day.month
                    dateHeaderYear.text = day.year
                    dateHeaderDayofweek.text = day.dayOfWeek
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(day: Day)
    }

    class DiffCallback : DiffUtil.ItemCallback<Day>() {

        override fun areItemsTheSame(oldItem: Day, newItem: Day): Boolean
            = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Day, newItem: Day): Boolean
            = oldItem == newItem
    }
}