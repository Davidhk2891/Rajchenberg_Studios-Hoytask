package com.rajchenbergstudios.hoytask.utils

import androidx.room.TypeConverter
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.rajchenbergstudios.hoytask.data.task.Task
import com.rajchenbergstudios.hoytask.data.taskinset.TaskInSet

class HTSKTypeConvUtils {

    // Serialize Task List
    @TypeConverter
    fun fromTaskListToJson(list: List<Task>): String {
        val mapper = jacksonObjectMapper()
        return mapper.writeValueAsString(list)
    }

    // Unserialize Task List
    @TypeConverter
    fun fromJsonToTaskList(json: String): List<Task> {
        val mapper = jacksonObjectMapper()
        return mapper.readValue(json)
    }

    // Serialize TaskInSet List
    @TypeConverter
    fun fromTaskSetListToJson(list: List<TaskInSet>): String {
        val mapper = jacksonObjectMapper()
        return mapper.writeValueAsString(list)
    }

    // Unserialize TaskInSet List
    @TypeConverter
    fun fromJsonToTaskSetList(json: String): List<TaskInSet> {
        val mapper = jacksonObjectMapper()
        return mapper.readValue(json)
    }
}