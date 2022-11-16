package com.rajchenbergstudios.hoygenda.data.day

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rajchenbergstudios.hoygenda.data.task.Task
import kotlinx.parcelize.Parcelize

@Entity(tableName = "day_table")
@Parcelize
data class Day(
    val dayOfWeek: String,
    val dayOfMonth: String,
    val month: String,
    val year: String,
    val listOfDays: List<Task> = emptyList(),
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) : Parcelable
