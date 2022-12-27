package com.rajchenbergstudios.hoygenda.ui.pastday.daydetailsjentrieslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntry
import com.rajchenbergstudios.hoygenda.databinding.SingleItemJournalEntryBinding

class PDJEntriesListAdapter : ListAdapter<JournalEntry, PDJEntriesListAdapter.PDJEntriesListViewHolder>(DiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PDJEntriesListViewHolder {
        val binding = SingleItemJournalEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PDJEntriesListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PDJEntriesListViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class PDJEntriesListViewHolder(private val binding: SingleItemJournalEntryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(journalEntry: JournalEntry) {
            binding.apply {
                itemJournalEntryContentTextview.text = journalEntry.content
                itemJournalEntryImportantImageview.isVisible = journalEntry.important
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<JournalEntry>() {

        override fun areItemsTheSame(oldItem: JournalEntry, newItem: JournalEntry): Boolean
                = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: JournalEntry, newItem: JournalEntry): Boolean
                = oldItem.id == newItem.id
    }
}