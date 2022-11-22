package com.rajchenbergstudios.hoygenda.utils

import androidx.room.TypeConverter
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.rajchenbergstudios.hoygenda.data.today.task.Task
import com.rajchenbergstudios.hoygenda.data.taskinset.TaskInSet

class HGDATypeConvUtils {

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