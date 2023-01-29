package com.rajchenbergstudios.hoygenda.data.today.task

import androidx.room.*
import com.rajchenbergstudios.hoygenda.data.prefs.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM task_table")
    suspend fun getTasksList(): List<Task>

    fun getTasks(searchQuery: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Task>> =
        when (sortOrder) {
            SortOrder.BY_NAME -> getTasksSortedByName(searchQuery, hideCompleted)
            SortOrder.BY_TIME -> getTasksSortedByTime(searchQuery, hideCompleted)
        }

    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed = 0) AND" +
            " title LIKE '%' || :searchQuery || '%' ORDER BY important DESC, title")
    fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed = 0) AND" +
            " title LIKE '%' || :searchQuery || '%' ORDER BY important DESC, created")
    fun getTasksSortedByTime(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task_table LIMIT 1")
    suspend fun checkFirstItemFromList(): List<Task>

    @Query("SELECT * FROM task_table WHERE completed = 1")
    suspend fun checkCompletedTasksInList(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM task_table WHERE completed = 1")
    suspend fun deleteAllCompleted()

    @Query("DELETE FROM task_table")
    suspend fun nukeTaskTable()
}