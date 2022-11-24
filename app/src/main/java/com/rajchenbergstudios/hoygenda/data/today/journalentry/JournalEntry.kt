package com.rajchenbergstudios.hoygenda.data.today.journalentry

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DateFormat

@Entity(tableName = "journal_table")
@Parcelize
class JournalEntry(
    val content: String,
    val important: Boolean = false,
    val created: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable {
    val createdTimeFormat: String
        get() = DateFormat.getTimeInstance().format(created)
}