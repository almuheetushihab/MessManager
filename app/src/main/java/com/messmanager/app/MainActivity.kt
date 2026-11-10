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
import com.messmanager.app.data.repository.UserSettings
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
            val userSettings by settingsRepository.settings.collectAsStateWithLifecycle(initialValue = UserSettings())

            LaunchedEffect(userSettings.languageCode) {
                val targetLocales = if (userSettings.languageCode == "system") {
                    LocaleListCompat.getEmptyLocaleList()
                } else {
                    LocaleListCompat.forLanguageTags(userSettings.languageCode)
                }
                if (AppCompatDelegate.getApplicationLocales().toLanguageTags() != targetLocales.toLanguageTags()) {
                    AppCompatDelegate.setApplicationLocales(targetLocales)
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
}
