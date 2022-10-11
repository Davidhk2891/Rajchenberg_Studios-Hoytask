package com.rajchenbergstudios.hoytask.data.taskinset

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.parcelize.Parcelize

@Entity(tableName = "task_in_set_table")
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
data class TaskInSet(
    val taskInSet: String,
    val taskInSetBigTitle: String?,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) : Parcelable
