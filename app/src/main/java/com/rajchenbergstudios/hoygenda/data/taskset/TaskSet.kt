package com.rajchenbergstudios.hoygenda.data.taskset

import android.os.Parcelable
import androidx.room.*
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.rajchenbergstudios.hoygenda.data.taskinset.TaskInSet
import kotlinx.parcelize.Parcelize

@Entity(tableName = "set_table")
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
data class TaskSet(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "listOfTasks") val listOfTasks: List<TaskInSet> = emptyList(),
    @ColumnInfo(name = "chosen") val chosen: Boolean = false,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) : Parcelable
