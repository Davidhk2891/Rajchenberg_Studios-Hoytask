package com.rajchenbergstudios.hoytask.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.rajchenbergstudios.hoytask.data.task.Task

class Converters {

    companion object {

        // Serialize
        fun fromTaskListToJson(taskList: List<Task>): String {
            val mapper = jacksonObjectMapper()
            return mapper.writeValueAsString(taskList)
        }

        // Unserialize
        fun fromJsonToTaskList(json: String): List<Task> {
            val mapper = jacksonObjectMapper()
            return mapper.readValue(json)
        }
    }
}