package com.rajchenbergstudios.hoytask.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

class Converters {

    companion object {

        // Serialize
        fun fromTaskListToJson(list: List<Any>): String {
            val mapper = jacksonObjectMapper()
            return mapper.writeValueAsString(list)
        }

        // Unserialize
        fun fromJsonToTaskList(json: String): List<Any> {
            val mapper = jacksonObjectMapper()
            return mapper.readValue(json)
        }
    }
}