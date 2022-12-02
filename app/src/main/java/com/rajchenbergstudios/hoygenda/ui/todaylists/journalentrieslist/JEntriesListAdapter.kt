package com.rajchenbergstudios.hoygenda.ui.todaylists.journalentrieslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntry
import com.rajchenbergstudios.hoygenda.databinding.SingleItemJournalEntryBinding

class JEntriesListAdapter : ListAdapter<JournalEntry, JEntriesListAdapter.JEntriesListViewHolder>(DiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JEntriesListViewHolder {

        val binding = SingleItemJournalEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JEntriesListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JEntriesListViewHolder, position: Int) {

        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class JEntriesListViewHolder(private val binding: SingleItemJournalEntryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(journalEntry: JournalEntry){

            binding.apply {
                val title = "${journalEntry.createdTimeFormat} | "
                itemJournalEntryTitleTextview.text = title
                itemJournalEntryContentTextview.text = journalEntry.content
                itemJournalEntryImportantImageview.isVisible = journalEntry.important
            }
        }
    }


    // No idea why areContentsTheSame function return expression gives error. Both .equals() and == are not liked by IDE
    class DiffCallback : DiffUtil.ItemCallback<JournalEntry>() {

        override fun areItemsTheSame(oldItem: JournalEntry, newItem: JournalEntry): Boolean
            = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: JournalEntry, newItem: JournalEntry): Boolean
            = oldItem.equals(newItem)
    }
}