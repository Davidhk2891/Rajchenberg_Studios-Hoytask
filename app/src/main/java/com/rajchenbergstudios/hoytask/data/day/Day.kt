package com.rajchenbergstudios.hoytask.data.day

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "day_table")
@Parcelize
data class Day(
    val dayOfWeek: String,
    val dayOfMonth: String,
    val month: String,
    val year: String,
    val bgColor: Int = 0,
    val tasksListJson: String = "",
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) : Parcelable {

}
