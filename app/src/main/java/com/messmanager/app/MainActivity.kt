package com.messmanager.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.messmanager.app.core.theme.MessTheme
import com.messmanager.app.data.repository.AppTheme
import com.messmanager.app.data.repository.SettingsRepository
import com.messmanager.app.navigation.MessApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userSettingsState by settingsRepository.settings.collectAsStateWithLifecycle(initialValue = null)
            val userSettings = userSettingsState ?: return@setContent

            LaunchedEffect(userSettings.languageCode) {
                if (shouldUpdateLocales(userSettings.languageCode)) {
                    val targetLocales = if (userSettings.languageCode == "system") {
                        LocaleListCompat.getEmptyLocaleList()
                    } else {
                        LocaleListCompat.forLanguageTags(userSettings.languageCode)
                    }
                    AppCompatDelegate.setApplicationLocales(targetLocales)
                }
            }

            LaunchedEffect(userSettings.themeConfig) {
                val targetNightMode = when (userSettings.themeConfig) {
                    AppTheme.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    AppTheme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                    AppTheme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                }
                if (AppCompatDelegate.getDefaultNightMode() != targetNightMode) {
                    AppCompatDelegate.setDefaultNightMode(targetNightMode)
                }
            }

            val darkTheme = when (userSettings.themeConfig) {
                AppTheme.SYSTEM -> isSystemInDarkTheme()
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }

            MessTheme(darkTheme = darkTheme) {
                MessApp()
            }
        }
    }

    private fun shouldUpdateLocales(targetLanguageCode: String): Boolean {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        return if (targetLanguageCode == "system") {
            !currentLocales.isEmpty
        } else {
            if (currentLocales.isEmpty) {
                true
            } else {
                val primaryLanguage = currentLocales[0]?.language.orEmpty()
                !primaryLanguage.equals(targetLanguageCode, ignoreCase = true)
            }
        }
    }
}
