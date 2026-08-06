package com.example.eduapp.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "eduapp_preferences")

/**
 * "Other" advanced feature: small persisted app preferences. Currently, stores
 * whether sound effects are on, and the last username used, so returning players
 * don't have to retype their name every session.
 *
 * This is an interface (rather than exposing the DataStore implementation directly)
 * so AppViewModel can be unit-tested against a simple in-memory fake instead of
 * needing a real Android Context.
 */
interface UserPreferencesRepository {
    val soundEnabled: Flow<Boolean>
    val lastUsername: Flow<String>
    suspend fun setSoundEnabled(enabled: Boolean)
    suspend fun setLastUsername(username: String)
}

class DataStoreUserPreferencesRepository(private val context: Context) : UserPreferencesRepository {

    private object Keys {
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val LAST_USERNAME = stringPreferencesKey("last_username")
    }

    override val soundEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SOUND_ENABLED] ?: true
    }

    override val lastUsername: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.LAST_USERNAME] ?: ""
    }

    override suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SOUND_ENABLED] = enabled }
    }

    override suspend fun setLastUsername(username: String) {
        context.dataStore.edit { it[Keys.LAST_USERNAME] = username }
    }
}
