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

enum class AppTheme {
    SYSTEM, LIGHT, DARK
}

enum class AppLanguage(val code: String) {
    SYSTEM("system"),
    ENGLISH("en"),
    BANGLA("bn");

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: SYSTEM
    }
}

data class UserSettings(
    val themeConfig: AppTheme = AppTheme.SYSTEM,
    val languageCode: String = "system",
    val messName: String = "",
    val cutoffTime: String? = null, // Format "HH:mm", e.g. "20:00" or null if disabled
    val adminOverride: Boolean = false,
)

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val settings: Flow<UserSettings> = dataStore.data.map { prefs ->
        UserSettings(
            themeConfig = prefs[KEY_THEME_CONFIG]?.let {
                runCatching { AppTheme.valueOf(it) }.getOrDefault(AppTheme.SYSTEM)
            } ?: AppTheme.SYSTEM,
            languageCode = prefs[KEY_LANGUAGE_CODE] ?: "system",
            messName = prefs[KEY_MESS_NAME] ?: "",
            cutoffTime = prefs[KEY_CUTOFF_TIME].takeIf { !it.isNullOrBlank() },
            adminOverride = prefs[KEY_ADMIN_OVERRIDE] ?: false,
        )
    }

    suspend fun setThemeConfig(theme: AppTheme) {
        dataStore.edit { prefs ->
            prefs[KEY_THEME_CONFIG] = theme.name
        }
    }

    suspend fun setLanguageCode(languageCode: String) {
        dataStore.edit { prefs ->
            prefs[KEY_LANGUAGE_CODE] = languageCode
        }
    }

    suspend fun setMessName(messName: String) {
        dataStore.edit { prefs ->
            prefs[KEY_MESS_NAME] = messName.trim()
        }
    }

    suspend fun setCutoffTime(cutoffTime: String?) {
        dataStore.edit { prefs ->
            if (cutoffTime.isNullOrBlank()) {
                prefs.remove(KEY_CUTOFF_TIME)
            } else {
                prefs[KEY_CUTOFF_TIME] = cutoffTime.trim()
            }
        }
    }

    suspend fun setAdminOverride(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_ADMIN_OVERRIDE] = enabled
        }
    }

    private companion object {
        val KEY_THEME_CONFIG = stringPreferencesKey("theme_config")
        val KEY_LANGUAGE_CODE = stringPreferencesKey("language_code")
        val KEY_MESS_NAME = stringPreferencesKey("mess_name")
        val KEY_CUTOFF_TIME = stringPreferencesKey("cutoff_time")
        val KEY_ADMIN_OVERRIDE = booleanPreferencesKey("admin_override")
    }
}
