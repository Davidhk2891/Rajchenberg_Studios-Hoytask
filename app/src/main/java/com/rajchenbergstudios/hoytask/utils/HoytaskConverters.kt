package com.rajchenbergstudios.hoytask.utils

import androidx.room.TypeConverter
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

class HoytaskConverters {

    // Serialize
    @TypeConverter
    fun fromTaskSetListToJson(list: List<Any>): String {
        val mapper = jacksonObjectMapper()
        return mapper.writeValueAsString(list)
    }

    // Unserialize
    @TypeConverter
    fun fromJsonToTaskSetList(json: String): List<Any> {
        val mapper = jacksonObjectMapper()
        return mapper.readValue(json)
    }
}