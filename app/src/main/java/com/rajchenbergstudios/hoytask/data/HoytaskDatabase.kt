package com.rajchenbergstudios.hoytask.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rajchenbergstudios.hoytask.data.task.Task
import com.rajchenbergstudios.hoytask.data.task.TaskDao
import com.rajchenbergstudios.hoytask.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class HoytaskDatabase : RoomDatabase(){

    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val database: Provider<HoytaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ): RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().taskDao()

            applicationScope.launch {
                dao.insert(Task("Work", important = true, completed = true))
                dao.insert(Task("Work on android", completed = true))
                dao.insert(Task("Read 20 pages", completed = true))
                dao.insert(Task("Talk to Sarai"))
                dao.insert(Task("Talk to Sarai's husband"))
                dao.insert(Task("Talk to Sarai's workout"))
                dao.insert(Task("Talk to Sarai's Prep dinner"))
                dao.insert(Task("Talk to Sarai's Prep Do Duolingo"))
            }
        }
    }
}