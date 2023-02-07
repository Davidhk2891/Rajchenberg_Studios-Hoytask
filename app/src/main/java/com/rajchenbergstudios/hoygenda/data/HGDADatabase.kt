package com.rajchenbergstudios.hoygenda.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rajchenbergstudios.hoygenda.data.day.Day
import com.rajchenbergstudios.hoygenda.data.day.DayDao
import com.rajchenbergstudios.hoygenda.data.taskset.TaskSetDao
import com.rajchenbergstudios.hoygenda.data.today.task.Task
import com.rajchenbergstudios.hoygenda.data.today.task.TaskDao
import com.rajchenbergstudios.hoygenda.data.taskinset.TaskInSet
import com.rajchenbergstudios.hoygenda.data.taskinset.TaskInSetDao
import com.rajchenbergstudios.hoygenda.data.taskset.TaskSet
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntry
import com.rajchenbergstudios.hoygenda.data.today.journalentry.JournalEntryDao
import com.rajchenbergstudios.hoygenda.di.ApplicationScope
import com.rajchenbergstudios.hoygenda.utils.HGDADateUtils
import com.rajchenbergstudios.hoygenda.utils.HGDATypeConvUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

// const val TAG = "HoytaskDatabase.kt"

@Database(entities = [Task::class, Day::class, TaskSet::class, TaskInSet::class, JournalEntry::class], version = 22, exportSchema = false)
@TypeConverters(HGDATypeConvUtils::class)
abstract class HGDADatabase : RoomDatabase(){

    abstract fun taskDao(): TaskDao
    abstract fun taskSetDao(): TaskSetDao
    abstract fun taskInSetDao(): TaskInSetDao
    abstract fun dayDao(): DayDao
    abstract fun journalEntryDao(): JournalEntryDao

    class Callback @Inject constructor(
        private val database: Provider<HGDADatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ): RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val taskDao = database.get().taskDao()
            val taskSetDao = database.get().taskSetDao()
            val taskInSetDao = database.get().taskInSetDao()
            val journalEntryDao = database.get().journalEntryDao()
            val dayDao = database.get().dayDao()

            suspend fun presetData(taskSetDao: TaskSetDao, taskInSetDao: TaskInSetDao) {
                val task1 = TaskInSet(PresetData.PRESET_DAILIES_TASK_IN_SET_1, PresetData.PRESET_DAILIES)
                val task2 = TaskInSet(PresetData.PRESET_DAILIES_TASK_IN_SET_2, PresetData.PRESET_DAILIES)
                val task3 = TaskInSet(PresetData.PRESET_DAILIES_TASK_IN_SET_3, PresetData.PRESET_DAILIES)
                val task4 = TaskInSet(PresetData.PRESET_DAILIES_TASK_IN_SET_4, PresetData.PRESET_DAILIES)

                val task5 = TaskInSet(PresetData.PRESET_WEEKENDS_TASK_IN_SET_1, PresetData.PRESET_WEEKENDS)
                val task6 = TaskInSet(PresetData.PRESET_WEEKENDS_TASK_IN_SET_2, PresetData.PRESET_WEEKENDS)
                val task7 = TaskInSet(PresetData.PRESET_WEEKENDS_TASK_IN_SET_3, PresetData.PRESET_WEEKENDS)
                val task8 = TaskInSet(PresetData.PRESET_WEEKENDS_TASK_IN_SET_4, PresetData.PRESET_WEEKENDS)

                val task9 = TaskInSet(PresetData.PRESET_MY_MORNING_ROUTINE_TASK_IN_SET_1, PresetData.PRESET_MY_MORNING_ROUTINE)
                val task10 = TaskInSet(PresetData.PRESET_MY_MORNING_ROUTINE_TASK_IN_SET_2, PresetData.PRESET_MY_MORNING_ROUTINE)
                val task11 = TaskInSet(PresetData.PRESET_MY_MORNING_ROUTINE_TASK_IN_SET_3, PresetData.PRESET_MY_MORNING_ROUTINE)
                val task12 = TaskInSet(PresetData.PRESET_MY_MORNING_ROUTINE_TASK_IN_SET_4, PresetData.PRESET_MY_MORNING_ROUTINE)

                // To insert in TaskInSetDao
                val listOfTasksDailies = listOf(task1, task2, task3, task4)
                val listOfTasksWeekends = listOf(task5, task6, task7, task8)
                val listOfTasksMorningRoutine = listOf(task9, task10, task11, task12)

                // To insert in TaskSetDao
                val set1 = TaskSet(PresetData.PRESET_DAILIES, listOfTasksDailies)
                val set2 = TaskSet(PresetData.PRESET_WEEKENDS, listOfTasksWeekends)
                val set3 = TaskSet(PresetData.PRESET_MY_MORNING_ROUTINE, listOfTasksMorningRoutine)

                taskDao.insert(Task(PresetData.PRESET_TASK, important = true))
                journalEntryDao.insert(JournalEntry(PresetData.PRESET_ENTRY, important = true))

                /*
                taskDao.insert(Task(PresetData.PRESET_TASK_2))
                taskDao.insert(Task(PresetData.PRESET_TASK_3))
                taskDao.insert(Task(PresetData.PRESET_TASK_4))
                taskDao.insert(Task(PresetData.PRESET_TASK_5))
                 */

                taskSetDao.apply {
                    insert(set1)
                    insert(set2)
                    insert(set3)
                }

                taskInSetDao.apply {
                    for (item in listOfTasksDailies) { insert(item) }
                    for (item in listOfTasksWeekends) { insert(item) }
                    for (item in listOfTasksMorningRoutine) { insert(item) }
                }
            }

            suspend fun testData(dayDao: DayDao) {

                // To insert in Day
                val taskForDay1 = Task(PresetData.PRESET_DAY_TASK_1, important = true, id = 1)
                val taskForDay2 = Task(PresetData.PRESET_DAY_TASK_2, important = false, id = 2)
                val taskForDay3 = Task(PresetData.PRESET_DAY_TASK_3, important = false, id = 3)
                val taskForDay4 = Task(PresetData.PRESET_DAY_TASK_4, important = false, id = 4)
                val taskForDay5 = Task(PresetData.PRESET_DAY_TASK_5, important = false, id = 5)
                val taskForDay6 = Task(PresetData.PRESET_DAY_TASK_6, important = false, id = 6)

                val journalEntryForDay1 = JournalEntry(PresetData.PRESET_DAY_JOURNAL_ENTRY_1, id = 1)
                val journalEntryForDay2 = JournalEntry(PresetData.PRESET_DAY_JOURNAL_ENTRY_2, important = true, id = 2)
                val journalEntryForDay3 = JournalEntry(PresetData.PRESET_DAY_JOURNAL_ENTRY_3, important = false, id = 3)
                val journalEntryForDay4 = JournalEntry(PresetData.PRESET_DAY_JOURNAL_ENTRY_4, important = false, id = 4)
                val journalEntryForDay5 = JournalEntry(PresetData.PRESET_DAY_JOURNAL_ENTRY_5, important = false, id = 5)
                val journalEntryForDay6 = JournalEntry(PresetData.PRESET_DAY_JOURNAL_ENTRY_6, important = false, id = 6)

                val tasksForDayList = listOf(taskForDay1, taskForDay2, taskForDay3, taskForDay4, taskForDay5, taskForDay6)
                val journalEntryForDayList = listOf(journalEntryForDay1, journalEntryForDay2, journalEntryForDay3, journalEntryForDay4, journalEntryForDay5, journalEntryForDay6)
                val day1 = Day(
                    HGDADateUtils.currentDayOfWeekFormatted,
                    HGDADateUtils.currentDayOfMonthFormatted,
                    HGDADateUtils.currentMonthFormatted,
                    HGDADateUtils.currentYearFormatted,
                    tasksForDayList, journalEntryForDayList)

                dayDao.insert(day1)
            }

            // Initial Task for current day
            applicationScope.launch {
                presetData(taskSetDao, taskInSetDao)
            }

            //Initial day (testing)
            applicationScope.launch {
                // testData(dayDao)
            }
        }
    }
}