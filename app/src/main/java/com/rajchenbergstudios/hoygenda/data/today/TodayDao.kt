package com.rajchenbergstudios.hoygenda.data.today

import androidx.room.*
import com.rajchenbergstudios.hoygenda.data.prefs.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface TodayDao {

    @Query("SELECT * FROM today_table")
    suspend fun getTodays(): List<Today>

    fun getTodays(searchQuery: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Today>> =
        when (sortOrder) {
            SortOrder.BY_NAME -> getTasksSortedByName(searchQuery, hideCompleted)
            SortOrder.BY_DATE -> getTasksSortedByDate(searchQuery, hideCompleted)
        }

    @Query("SELECT * FROM today_table WHERE (completed != :hideCompleted OR completed = 0) AND" +
            " content LIKE '%' || :searchQuery || '%' ORDER BY important DESC, content"
    )
    fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Today>>

    @Query("SELECT * FROM today_table WHERE (completed != :hideCompleted OR completed = 0) AND" +
            " content LIKE '%' || :searchQuery || '%' ORDER BY important DESC, created"
    )
    fun getTasksSortedByDate(searchQuery: String, hideCompleted: Boolean): Flow<List<Today>>

    @Query("SELECT * FROM today_table LIMIT 1")
    suspend fun firstItemFromList(): List<Today>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(today: Today)

    @Update
    suspend fun update(today: Today)

    @Delete
    suspend fun delete(today: Today)

    @Query("DELETE FROM today_table WHERE completed = 1")
    suspend fun deleteAllCompleted()

    @Query("DELETE FROM today_table")
    suspend fun nukeTaskTable()
}