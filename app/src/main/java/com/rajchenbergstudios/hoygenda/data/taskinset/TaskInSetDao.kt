package com.rajchenbergstudios.hoygenda.data.taskinset

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskInSetDao {

    @Query("SELECT * FROM task_in_set_table WHERE taskInSetBigTitle LIKE '%' || :setTitle || '%' ORDER BY id ASC")
    fun getTasksInSet(setTitle: String): Flow<List<TaskInSet>>

    @Query("SELECT * FROM task_in_set_table WHERE taskInSetBigTitle LIKE '%' || :setTitle || '%' ORDER BY id ASC")
    suspend fun getTasksFromSet(setTitle: String): List<TaskInSet>

    @Query("DELETE FROM task_in_set_table")
    suspend fun deleteAllTasksInSets()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskInSet)

    @Update
    suspend fun update(task: TaskInSet)

    @Delete
    suspend fun delete(task: TaskInSet)
}