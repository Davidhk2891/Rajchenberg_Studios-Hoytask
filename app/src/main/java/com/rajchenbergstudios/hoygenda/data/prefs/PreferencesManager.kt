package com.rajchenbergstudios.hoygenda.data.prefs

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

enum class SortOrder{BY_TIME, BY_NAME}

data class FilterTodayPreferences(val sortOrder: SortOrder, val hideCompleted: Boolean)

data class FilterPastDayPreferences(val sortOrder: SortOrder)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context){

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    private val dataStore = context.dataStore

    // Read data from DataStore
    val todayPreferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val sortOrder = SortOrder.valueOf(
                preferences[PreferencesKeys.TODAY_SORT_ORDER] ?: SortOrder.BY_TIME.name
            )
            val hideCompleted = preferences[PreferencesKeys.TODAY_HIDE_COMPLETED] ?: false

            FilterTodayPreferences(sortOrder, hideCompleted)
        }

    val pastDayPreferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val sortOrder = SortOrder.valueOf(
                preferences[PreferencesKeys.PAST_DAY_SORT_ORDER] ?: SortOrder.BY_TIME.name
            )

            FilterPastDayPreferences(sortOrder)
        }

    suspend fun isTutorialAutoRun(): Boolean? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.TUTORIAL_AUTO_RUN]
        }.first()
    }

    // Write data to DataStore
    suspend fun setTutorialAutoRunSettingKey() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TUTORIAL_AUTO_RUN] = true
        }
    }

    suspend fun updateSortOrder(sortOrder: SortOrder) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TODAY_SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateHideCompleted(hideCompleted: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TODAY_HIDE_COMPLETED] = hideCompleted
        }
    }

    suspend fun updatePastDaySortOrder(sortOrder: SortOrder) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PAST_DAY_SORT_ORDER] = sortOrder.name
        }
    }

    private object PreferencesKeys {
        val TODAY_SORT_ORDER = stringPreferencesKey("sort_order")
        val TODAY_HIDE_COMPLETED = booleanPreferencesKey("hide_completed")
        val PAST_DAY_SORT_ORDER = stringPreferencesKey("past_day_sort_order")
        val TUTORIAL_AUTO_RUN = booleanPreferencesKey("tutorial_auto_run")
    }
}