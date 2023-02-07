package com.rajchenbergstudios.hoygenda.data.day

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DayDao {

    @Query("SELECT * FROM day_table ORDER BY id DESC")
    fun getDays(): Flow<List<Day>>

    @Insert
    suspend fun insert(day: Day)

    @Query("SELECT * FROM day_table LIMIT 1")
    suspend fun checkFirstItemFromList(): List<Day>

    @Query("DELETE FROM day_table")
    suspend fun nukeDayTable()
}