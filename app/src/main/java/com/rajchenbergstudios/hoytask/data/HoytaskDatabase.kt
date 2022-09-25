package com.rajchenbergstudios.hoytask.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rajchenbergstudios.hoytask.data.day.Day
import com.rajchenbergstudios.hoytask.data.day.DayDao
import com.rajchenbergstudios.hoytask.data.taskset.TaskSetDao
import com.rajchenbergstudios.hoytask.data.task.Task
import com.rajchenbergstudios.hoytask.data.task.TaskDao
import com.rajchenbergstudios.hoytask.data.taskset.TaskSet
import com.rajchenbergstudios.hoytask.di.ApplicationScope
import com.rajchenbergstudios.hoytask.util.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class, Day::class, TaskSet::class], version = 6, exportSchema = false)
abstract class HoytaskDatabase : RoomDatabase(){

    abstract fun taskDao(): TaskDao
    abstract fun taskSetDao(): TaskSetDao
    abstract fun dayDao(): DayDao

    class Callback @Inject constructor(
        private val database: Provider<HoytaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ): RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val taskDao = database.get().taskDao()
            val taskSetDao = database.get().taskSetDao()

            // Initial Task for current day
            applicationScope.launch {
                taskDao.insert(Task("Start setting up your tasks", important = true))
            }

            //Initial set of tasks (testing)
            applicationScope.launch {

                val task1 = Task("Workout for 4 hours", false, false)
                val task2 = Task("Do Duolingo", false, false)
                val task3 = Task("Read 10 pages", false, false)
                val listOfSets = listOf(task1, task2, task3)
                val set1 = TaskSet("Dailies", Converters.fromTaskListToJson(listOfSets))
                val set2 = TaskSet("Weekends", Converters.fromTaskListToJson(listOfSets))
                val set3 = TaskSet("Morning routine", Converters.fromTaskListToJson(listOfSets))
                taskSetDao.insert(set1)
                taskSetDao.insert(set2)
                taskSetDao.insert(set3)
            }
        }
    }
}