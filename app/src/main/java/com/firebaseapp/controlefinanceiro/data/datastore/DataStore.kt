package com.firebaseapp.controlefinanceiro.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesDataStore(private val context: Context) {

    // [START android_datastore_preferences_read]
    fun counterFlow(): Flow<Int> =
        context.dataStore.data.map { preferences -> preferences[EXAMPLE_COUNTER] ?: 0 }

    fun hapticEnabledFlow(): Flow<Boolean> =
        context.dataStore.data.map { preferences -> preferences[HAPTIC_ENABLED] ?: true }

    // [END android_datastore_preferences_read]

    // [START android_datastore_preferences_write]
    suspend fun incrementCounter() {
        context.dataStore.updateData {
            it.toMutablePreferences().also { preferences ->
                preferences[EXAMPLE_COUNTER] = (preferences[EXAMPLE_COUNTER] ?: 0) + 1
            }
        }
    }

    suspend fun updateHaptic(enabled: Boolean) {
        context.dataStore.updateData {
            it.toMutablePreferences().also { preferences ->
                preferences[HAPTIC_ENABLED] = enabled
            }
        }
    }


    // [END android_datastore_preferences_write]

    /**
     * Write example with helper function that is similar to
     * [SharedPreferences.edit](https://developer.android.com/reference/android/content/SharedPreferences#edit())
     */
    // [START android_datastore_preferences_write_edit]
    suspend fun incrementCounterWithEdit() {
        context.dataStore.edit { preferences ->
            preferences[EXAMPLE_COUNTER] = (preferences[EXAMPLE_COUNTER] ?: 0) + 1
        }
    }

    // [END android_datastore_preferences_write_edit]

    companion object {
        // [START android_datastore_preferences_key]
        val EXAMPLE_COUNTER = intPreferencesKey("example_counter")

        val HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        // [END android_datastore_preferences_key]
    }
}