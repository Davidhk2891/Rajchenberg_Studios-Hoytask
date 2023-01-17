package com.rajchenbergstudios.hoygenda.data.prefs

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

enum class SortOrder{BY_TIME, BY_NAME}

data class FilterPreferences(val sortOrder: SortOrder, val hideCompleted: Boolean)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context){

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    private val dataStore = context.dataStore

    // Read data from DataStore
    val preferencesFlow = dataStore.data
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
                preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.BY_TIME.name
            )
            val hideCompleted = preferences[PreferencesKeys.HIDE_COMPLETED] ?: false

            FilterPreferences(sortOrder, hideCompleted)
        }

    suspend fun getDaySavingSetting(): Boolean? {
        var isDaySavingSet: Boolean? = null
        dataStore.data.map { preferences ->
            isDaySavingSet = preferences[InternalPreferencesKeys.DAY_SAVING_SET]
        }.firstOrNull()
        return isDaySavingSet
    }

    // Write data to DataStore
    suspend fun updateSortOrder(sortOrder: SortOrder) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateHideCompleted(hideCompleted: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HIDE_COMPLETED] = hideCompleted
        }
    }

    suspend fun setDaySavingSetting() {
        dataStore.edit { preferences ->
            preferences[InternalPreferencesKeys.DAY_SAVING_SET] = true
        }
    }

    private object PreferencesKeys {
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val HIDE_COMPLETED = booleanPreferencesKey("hide_completed")
    }

    private object InternalPreferencesKeys {
        val DAY_SAVING_SET = booleanPreferencesKey("day_saving_set")
    }
}