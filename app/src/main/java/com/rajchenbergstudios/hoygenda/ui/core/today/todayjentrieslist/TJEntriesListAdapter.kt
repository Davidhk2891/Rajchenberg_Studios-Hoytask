package com.rajchenbergstudios.hoygenda.ui.core.today.todayjentrieslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntry
import com.rajchenbergstudios.hoygenda.databinding.SingleItemJournalEntryBinding

class TJEntriesListAdapter(private val listener: OnItemClickListener) : ListAdapter<JournalEntry, TJEntriesListAdapter.JEntriesListViewHolder>(
    DiffCallback()
){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JEntriesListViewHolder {

        val binding = SingleItemJournalEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JEntriesListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JEntriesListViewHolder, position: Int) {

        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class JEntriesListViewHolder(private val binding: SingleItemJournalEntryBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val jEntry = getItem(position)
                        listener.onItemClick(jEntry)
                    }
                }
                root.setOnLongClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val jEntry = getItem(position)
                        listener.onItemLongClick(jEntry)
                    }
                    return@setOnLongClickListener true
                }
            }
        }

        fun bind(journalEntry: JournalEntry){

            binding.apply {
                val title = "${journalEntry.createdTimeFormat} | "
                itemJournalEntryTitleTextview.text = title
                itemJournalEntryContentTextview.text = journalEntry.content
                itemJournalEntryImportantImageview.isVisible = journalEntry.important
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(journalEntry: JournalEntry)
        fun onItemLongClick(journalEntry: JournalEntry)
    }

    // No idea why areContentsTheSame function return expression gives error. Both .equals() and == are not liked by IDE
    class DiffCallback : DiffUtil.ItemCallback<JournalEntry>() {

        override fun areItemsTheSame(oldItem: JournalEntry, newItem: JournalEntry): Boolean
            = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: JournalEntry, newItem: JournalEntry): Boolean
            = oldItem.equals(newItem)
    }
}