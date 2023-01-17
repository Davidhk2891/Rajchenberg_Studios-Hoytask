package com.rajchenbergstudios.hoygenda.data.today.task

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.parcelize.Parcelize
import java.text.DateFormat

@Entity(tableName = "task_table")
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
data class Task(
    val title: String,
    val important: Boolean = false,
    val completed: Boolean = false,
    val created: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable {
    val createdTimeFormat: String
        get() = DateFormat.getTimeInstance(DateFormat.SHORT).format(created)
}
