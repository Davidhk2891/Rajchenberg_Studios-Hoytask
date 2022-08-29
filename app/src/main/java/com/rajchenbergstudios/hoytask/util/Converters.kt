package com.rajchenbergstudios.hoytask.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.rajchenbergstudios.hoytask.data.task.Task

class Converters {

    companion object {

        // Serialize
        fun fromTaskListToJson(taskList: List<Task>): String {
            return ObjectMapper().writeValueAsString(taskList)
        }

        // Unserialize
        fun fromJsonToTaskList(json: String): List<Task> {
            return listOf(ObjectMapper().readValue(json, Task::class.java))
        }
    }
}