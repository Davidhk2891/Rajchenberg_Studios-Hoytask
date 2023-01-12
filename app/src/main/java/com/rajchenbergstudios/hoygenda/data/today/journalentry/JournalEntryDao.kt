package com.rajchenbergstudios.hoygenda.data.today.journalentry

import androidx.room.*
import com.rajchenbergstudios.hoygenda.data.prefs.SortOrder
import com.rajchenbergstudios.hoygenda.data.today.task.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalEntryDao {

    @Query("SELECT * FROM journal_table")
    suspend fun getJournalEntriesList(): List<JournalEntry>

    fun getJournalEntries(searchQuery: String, sortOrder: SortOrder): Flow<List<JournalEntry>> =
        when (sortOrder) {
            SortOrder.BY_NAME -> getJournalEntriesByAlphabet(searchQuery)
            SortOrder.BY_TIME -> getJournalEntriesByTime(searchQuery)
        }

    @Query("SELECT * FROM journal_table WHERE content LIKE '%' || :searchQuery || '%' ORDER BY important DESC, content")
    fun getJournalEntriesByAlphabet(searchQuery: String): Flow<List<JournalEntry>>

    @Query("SELECT * FROM journal_table WHERE content LIKE '%' || :searchQuery || '%' ORDER BY important DESC, created")
    fun getJournalEntriesByTime(searchQuery: String): Flow<List<JournalEntry>>

    @Query("SELECT * FROM journal_table LIMIT 1")
    suspend fun firstItemFromList(): List<JournalEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(journalEntry: JournalEntry)

    @Update
    suspend fun update(journalEntry: JournalEntry)

    @Delete
    suspend fun delete(journalEntry: JournalEntry)

    @Query("DELETE FROM journal_table")
    suspend fun nukeJEntryTable()
}