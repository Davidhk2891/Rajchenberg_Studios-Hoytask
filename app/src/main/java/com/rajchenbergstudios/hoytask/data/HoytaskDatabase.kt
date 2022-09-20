package com.rajchenbergstudios.hoytask.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rajchenbergstudios.hoytask.data.day.Day
import com.rajchenbergstudios.hoytask.data.day.DayDao
import com.rajchenbergstudios.hoytask.data.task.Task
import com.rajchenbergstudios.hoytask.data.task.TaskDao
import com.rajchenbergstudios.hoytask.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class, Day::class], version = 5, exportSchema = false)
abstract class HoytaskDatabase : RoomDatabase(){

    abstract fun taskDao(): TaskDao
    abstract fun dayDao(): DayDao

    class Callback @Inject constructor(
        private val database: Provider<HoytaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ): RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val taskDao = database.get().taskDao()
            applicationScope.launch {
                taskDao.insert(Task("Start setting up your tasks", important = true))
            }
        }
    }
}