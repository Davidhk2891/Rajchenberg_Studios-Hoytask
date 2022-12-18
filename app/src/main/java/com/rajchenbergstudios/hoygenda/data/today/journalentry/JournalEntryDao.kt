package com.rajchenbergstudios.hoygenda.data.today.journalentry

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalEntryDao {

    @Query("SELECT * FROM journal_table")
    suspend fun getTodays(): List<JournalEntry>

    /*
    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed = 0) AND" +
            " title LIKE '%' || :searchQuery || '%' ORDER BY important DESC, title")
     */

    @Query("SELECT * FROM journal_table ORDER BY important DESC, created")
    fun getJournalEntries(): Flow<List<JournalEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(journalEntry: JournalEntry)

    @Update
    suspend fun update(journalEntry: JournalEntry)

    @Delete
    suspend fun delete(journalEntry: JournalEntry)

    @Query("DELETE FROM journal_table")
    suspend fun nukeJEntryTable()
}