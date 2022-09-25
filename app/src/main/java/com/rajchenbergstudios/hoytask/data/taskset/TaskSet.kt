package com.rajchenbergstudios.hoytask.data.taskset

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.parcelize.Parcelize

@Entity(tableName = "set_table")
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
data class TaskSet(
    val title: String,
    val setsListJson: String = "",
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) : Parcelable {

}
