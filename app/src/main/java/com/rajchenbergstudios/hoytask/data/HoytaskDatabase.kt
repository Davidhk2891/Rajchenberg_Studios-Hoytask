package com.rajchenbergstudios.hoytask.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rajchenbergstudios.hoytask.data.day.Day
import com.rajchenbergstudios.hoytask.data.day.DayDao
import com.rajchenbergstudios.hoytask.data.task.Task
import com.rajchenbergstudios.hoytask.data.task.TaskDao
import com.rajchenbergstudios.hoytask.di.ApplicationScope
import com.rajchenbergstudios.hoytask.util.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class, Day::class], version = 3, exportSchema = false)
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
            val dayDao = database.get().dayDao()

            applicationScope.launch {

                val task1 = Task("Work", important = true, completed = true)
                val task2 = Task("Play", important = true, completed = true)
                val task3 = Task("sleep", important = true)

                val tasks = listOf(task1, task2, task3)

                taskDao.insert(Task("Work", important = true, completed = true))
                dayDao.insert(Day("Friday", "26", "August", "2022", 1, Converters.fromTaskListToJson(tasks)))
                dayDao.insert(Day("Saturday", "27", "August", "2022", 1, Converters.fromTaskListToJson(tasks)))
                dayDao.insert(Day("Sunday", "28", "August", "2022", 1, Converters.fromTaskListToJson(tasks)))
            }
        }
    }
}