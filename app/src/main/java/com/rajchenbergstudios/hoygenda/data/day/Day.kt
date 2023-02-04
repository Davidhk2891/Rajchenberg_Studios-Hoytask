package com.rajchenbergstudios.hoygenda.data.day

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntry
import com.rajchenbergstudios.hoygenda.data.today.task.Task
import com.rajchenbergstudios.hoygenda.utils.Logger
import kotlinx.parcelize.Parcelize
private const val TAG = "Day"
@Entity(tableName = "day_table")
@Parcelize
data class Day(
    val dayOfWeek: String,
    var dayOfMonth: String,
    val month: String,
    val year: String,
    val listOfTasks: List<Task> = emptyList(),
    val listOfJEntries: List<JournalEntry> = emptyList(),
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) : Parcelable {
    fun formattedDayOfMonth(): String {
        val formattedDayOfMonth = if (dayOfMonth.toInt() < 10) {
            "0$dayOfMonth"
        } else {
            dayOfMonth
        }
        Logger.i(TAG, " formattedDayMonth", formattedDayOfMonth)
        return formattedDayOfMonth
    }
}
