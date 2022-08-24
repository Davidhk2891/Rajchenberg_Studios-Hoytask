package com.rajchenbergstudios.hoytask.data.day

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rajchenbergstudios.hoytask.data.task.Task
import kotlinx.parcelize.Parcelize

@Entity(tableName = "day_table")
@Parcelize
data class Day(
    val dayOfWeek: String,
    val dateOfDay: String,
    val month: String,
    val year: String,
    val bgColor: String,
    val tasksList: List<Task>,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) : Parcelable {

}
