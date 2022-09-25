package com.rajchenbergstudios.hoytask.data.taskset

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskSetDao {

    @Query("SELECT * FROM set_table")
    fun getSets(): Flow<List<TaskSet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(set: TaskSet)

    @Update
    suspend fun update(set: TaskSet)

    @Delete
    suspend fun delete(set: TaskSet)
}