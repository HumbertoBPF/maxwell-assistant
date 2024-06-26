package com.example.maxwell.data_store

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.maxwell.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class Settings(private val context: Context) {
    companion object {
        const val USERNAME_KEY = "username_key"
        const val DAILY_SYNC_ENABLED_KEY = "daily_sync_enabled"
        const val ID_TOKEN = "id_token"
        const val ID_TOKEN_EXPIRATION = "id_token_expiration"
        const val LAST_BACKUP_TIMESTAMP = "last_backup"
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

    fun isDailySyncEnabled(): Flow<Boolean?> {
        return context.dataStore.data.map { preferences ->
            val dailySyncEnabledKey = booleanPreferencesKey(DAILY_SYNC_ENABLED_KEY)
            val dailySyncEnabled = preferences[dailySyncEnabledKey]
            dailySyncEnabled
        }
    }

    suspend fun setDailySyncEnabled(dailySyncEnabled: Boolean?) {
        val dailySyncEnabledKey = booleanPreferencesKey(DAILY_SYNC_ENABLED_KEY)

        context.dataStore.edit { settings ->
            if (dailySyncEnabled == null) {
                settings.remove(dailySyncEnabledKey)
            } else {
                settings[dailySyncEnabledKey] = dailySyncEnabled
            }
        }
    }

    fun getIdToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            val idTokenKey = stringPreferencesKey(ID_TOKEN)
            val idToken = preferences[idTokenKey]
            idToken
        }
    }

    fun getIdTokenExpiration(): Flow<Long?> {
        return context.dataStore.data.map { preferences ->
            val idTokenKeyExpiration = longPreferencesKey(ID_TOKEN_EXPIRATION)
            val idTokenExpiration = preferences[idTokenKeyExpiration]
            idTokenExpiration
        }
    }

    suspend fun setIdToken(idToken: String) {
        val idTokenKey = stringPreferencesKey(ID_TOKEN)
        val idTokenKeyExpiration = longPreferencesKey(ID_TOKEN_EXPIRATION)

        context.dataStore.edit { settings ->
            settings[idTokenKey] = idToken
            settings[idTokenKeyExpiration] = Calendar.getInstance().timeInMillis + 3500 * 1000
        }
    }

    fun getLastBackupTimestamp(): Flow<Long?> {
        return context.dataStore.data.map { preferences ->
            val lastBackupTimestampPreferenceKey = longPreferencesKey(LAST_BACKUP_TIMESTAMP)
            val lastBackupTimestamp = preferences[lastBackupTimestampPreferenceKey]
            lastBackupTimestamp
        }
    }

    suspend fun setLastBackupTimestamp(lastBackupTimestamp: Long) {
        val lastBackupTimestampPreferenceKey = longPreferencesKey(LAST_BACKUP_TIMESTAMP)

        context.dataStore.edit { settings ->
            settings[lastBackupTimestampPreferenceKey] = lastBackupTimestamp
        }
    }
}