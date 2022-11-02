package com.rajchenbergstudios.hoytask.data.taskset

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskSetDao {

    @Query("SELECT * FROM set_table")
    fun getSets(): Flow<List<TaskSet>>

    @Query("SELECT * FROM set_table WHERE title LIKE '%' || :searchQuery || '%' ORDER BY id ASC")
    fun getSets(searchQuery: String): Flow<List<TaskSet>>

    @Query("SELECT * FROM set_table WHERE chosen = :isChecked")
    suspend fun getChosenSets(isChecked: Boolean): List<TaskSet>

    @Query("DELETE FROM set_table")
    suspend fun deleteAllSets()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(set: TaskSet)

    @Update
    suspend fun update(set: TaskSet)

    @Delete
    suspend fun delete(set: TaskSet)
}