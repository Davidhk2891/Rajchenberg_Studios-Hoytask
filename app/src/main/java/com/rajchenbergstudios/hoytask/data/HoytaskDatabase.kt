package com.rajchenbergstudios.hoytask.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rajchenbergstudios.hoytask.data.day.Day
import com.rajchenbergstudios.hoytask.data.day.DayDao
import com.rajchenbergstudios.hoytask.data.taskset.TaskSetDao
import com.rajchenbergstudios.hoytask.data.task.Task
import com.rajchenbergstudios.hoytask.data.task.TaskDao
import com.rajchenbergstudios.hoytask.data.task.TaskTypeConverter
import com.rajchenbergstudios.hoytask.data.taskinset.TaskInSet
import com.rajchenbergstudios.hoytask.data.taskinset.TaskInSetDao
import com.rajchenbergstudios.hoytask.data.taskset.TaskInSetTypeConverter
import com.rajchenbergstudios.hoytask.data.taskset.TaskSet
import com.rajchenbergstudios.hoytask.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

// const val TAG = "HoytaskDatabase.kt"

@Database(entities = [Task::class, Day::class, TaskSet::class, TaskInSet::class], version = 14, exportSchema = false)
@TypeConverters(TaskTypeConverter::class, TaskInSetTypeConverter::class)
abstract class HoytaskDatabase : RoomDatabase(){

    abstract fun taskDao(): TaskDao
    abstract fun taskSetDao(): TaskSetDao
    abstract fun taskInSetDao(): TaskInSetDao
    abstract fun dayDao(): DayDao

    class Callback @Inject constructor(
        private val database: Provider<HoytaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ): RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val taskDao = database.get().taskDao()
            val taskSetDao = database.get().taskSetDao()
            val taskInSetDao = database.get().taskInSetDao()

            // Initial Task for current day
            applicationScope.launch {
                taskDao.insert(Task("Start setting up your tasks", important = true))
            }

            //Initial set of tasks (testing)
            applicationScope.launch {

                val task1 = TaskInSet("Workout for 4 hours", "Dailies")
                val task2 = TaskInSet("Do Duolingo", "Dailies")
                val task3 = TaskInSet("Read 10 pages", "Dailies")
                val task4 = TaskInSet("Work on Android", "Dailies")

                val task5 = TaskInSet("Spend time with Rossy", "Weekends")
                val task6 = TaskInSet("Read your book", "Weekends")
                val task7 = TaskInSet("Relax", "Weekends")
                val task8 = TaskInSet("Sleep in", "Weekends")

                val task9 = TaskInSet("Work on the app first thing", "Morning routine")
                val task10 = TaskInSet("do your German classes", "Morning routine")
                val task11 = TaskInSet("Read your book", "Morning routine")
                val task12 = TaskInSet("Get some work done", "Morning routine")

                // To insert in TaskInSetDao
                val listOfTasksDailies = listOf(task1, task2, task3, task4)
                val listOfTasksWeekends = listOf(task5, task6, task7, task8)
                val listOfTasksMorningRoutine = listOf(task9, task10, task11, task12)

                // To insert in TaskSetDao
                val set1 = TaskSet("Dailies", listOfTasksDailies)
                val set2 = TaskSet("Weekends", listOfTasksWeekends)
                val set3 = TaskSet("Morning routine", listOfTasksMorningRoutine)

                taskSetDao.apply {
                    insert(set1)
                    insert(set2)
                    insert(set3)

                    var i = 0
                    repeat(20){
                        i += 1
                        val testTask1 = TaskInSet("Do thing 1", "Set $i")
                        val testTask2 = TaskInSet("Do thing 2", "Set $i")
                        val testTask3 = TaskInSet("Do thing 3", "Set $i")
                        val listOfTestTasks = listOf(testTask1, testTask2, testTask3)
                        val set = TaskSet("Set $i", listOfTestTasks)
                        insert(set)
                        for (item in listOfTestTasks) {
                            taskInSetDao.insert(item)
                        }
                    }
                }

                taskInSetDao.apply {
                    for (item in listOfTasksDailies) { insert(item) }
                    for (item in listOfTasksWeekends) { insert(item) }
                    for (item in listOfTasksMorningRoutine) { insert(item) }
                }
            }
        }
    }
}