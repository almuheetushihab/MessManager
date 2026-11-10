package com.messmanager.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class UserSettings(
    val cutoffTime: String? = null, // Format "HH:mm", e.g. "20:00" or null if disabled
    val adminOverride: Boolean = false,
)

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val settings: Flow<UserSettings> = dataStore.data.map { prefs ->
        UserSettings(
            cutoffTime = prefs[KEY_CUTOFF_TIME].takeIf { !it.isNull_or_blank() },
            adminOverride = prefs[KEY_ADMIN_OVERRIDE] ?: false,
        )
    }

    suspend fun setCutoffTime(cutoffTime: String?) {
        dataStore.edit { prefs ->
            if (cutoffTime.isNullOrBlank()) {
                prefs.remove(KEY_CUTOFF_TIME)
            } else {
                prefs[KEY_CUTOFF_TIME] = cutoffTime
            }
        }
    }

    suspend fun setAdminOverride(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_ADMIN_OVERRIDE] = enabled
        }
    }

    private companion object {
        val KEY_CUTOFF_TIME = stringPreferencesKey("cutoff_time")
        val KEY_ADMIN_OVERRIDE = booleanPreferencesKey("admin_override")
    }
}

private fun String?.isNull_or_blank(): Boolean = this == null || this.trim().isEmpty()
