package com.rajchenbergstudios.hoytask.data.taskset

import android.os.Parcelable
import androidx.room.*
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.rajchenbergstudios.hoytask.data.taskinset.TaskInSet
import kotlinx.parcelize.Parcelize

@Entity(tableName = "set_table")
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
data class TaskSet(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "listOfTasks") val listOfTasks: List<TaskInSet> = emptyList(),
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) : Parcelable

class TaskInSetTypeConverter {

    // Serialize
    @TypeConverter
    fun fromTaskSetListToJson(list: List<TaskInSet>): String {
        val mapper = jacksonObjectMapper()
        return mapper.writeValueAsString(list)
    }

    // Unserialize
    @TypeConverter
    fun fromJsonToTaskSetList(json: String): List<TaskInSet> {
        val mapper = jacksonObjectMapper()
        return mapper.readValue(json)
    }
}
