package com.rajchenbergstudios.hoytask.data.day

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DayDao {

    @Query("SELECT * FROM day_table")
    fun getDays(): Flow<List<Day>>

    @Insert
    suspend fun insert(day: Day)
}