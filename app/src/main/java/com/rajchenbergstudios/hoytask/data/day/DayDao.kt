package com.rajchenbergstudios.hoytask.data.day

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DayDao {

    @Query("SELECT * FROM day_table")
    suspend fun getDays(): List<Day>

    @Insert
    suspend fun insert(day: Day)
}