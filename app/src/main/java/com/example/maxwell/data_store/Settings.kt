package com.example.maxwell.data_store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.maxwell.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Settings(private val context: Context) {
    companion object {
        const val USERNAME_KEY = "username_key"
        const val PREFERENCES_KEY = "preferences_key"
        private val Context.dataStore by preferencesDataStore(name = BuildConfig.NAME_PREFERENCES_DATA_STORE)
    }

    fun getUsername(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            val usernamePreferencesKey = stringPreferencesKey(USERNAME_KEY)
            val username = preferences[usernamePreferencesKey]
            username
        }
    }

    suspend fun setUsername(username: String) {
        val usernamePreferencesKey = stringPreferencesKey(USERNAME_KEY)

        context.dataStore.edit { settings ->
            settings[usernamePreferencesKey] = username
        }
    }

    fun getDailySynchronizationTime(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            val dailySynchronizationTimeKey = stringPreferencesKey(PREFERENCES_KEY)
            val dailySynchronizationTime = preferences[dailySynchronizationTimeKey]
            dailySynchronizationTime
        }
    }

    suspend fun setDailySynchronizationTime(dailySynchronizationTime: String?) {
        val dailySynchronizationTimeKey = stringPreferencesKey(PREFERENCES_KEY)

        context.dataStore.edit { settings ->
            if (dailySynchronizationTime == null) {
                settings.remove(dailySynchronizationTimeKey)
            } else {
                settings[dailySynchronizationTimeKey] = dailySynchronizationTime
            }
        }
    }
}