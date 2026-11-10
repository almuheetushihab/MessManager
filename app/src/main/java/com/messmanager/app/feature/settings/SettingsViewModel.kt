package com.messmanager.app.feature.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messmanager.app.R
import com.messmanager.app.core.util.UiText
import com.messmanager.app.data.repository.AppTheme
import com.messmanager.app.data.repository.DatabaseBackupRepository
import com.messmanager.app.data.repository.SettingsRepository
import com.messmanager.app.data.repository.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val settings: UserSettings = UserSettings(),
    val showRestoreConfirmDialog: Boolean = false,
    val pendingImportUri: Uri? = null,
    val isProcessing: Boolean = false,
    val snackbarMessage: UiText? = null,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val backupRepository: DatabaseBackupRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { userSettings ->
                _uiState.update { it.copy(settings = userSettings) }
            }
        }
    }

    fun setThemeConfig(theme: AppTheme) {
        viewModelScope.launch { settingsRepository.setThemeConfig(theme) }
    }

    fun setLanguageCode(code: String) {
        viewModelScope.launch { settingsRepository.setLanguageCode(code) }
    }

    fun setMessName(name: String) {
        viewModelScope.launch { settingsRepository.setMessName(name) }
    }

    fun setCutoffTime(cutoffTime: String?) {
        viewModelScope.launch { settingsRepository.setCutoffTime(cutoffTime) }
    }

    fun setAdminOverride(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setAdminOverride(enabled) }
    }

    fun exportDatabase(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            val result = backupRepository.exportDatabaseToJsonUri(uri)
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    snackbarMessage = if (result.isSuccess) {
                        UiText.Resource(R.string.settings_export_success)
                    } else {
                        UiText.Resource(
                            R.string.settings_export_failed,
                            result.exceptionOrNull()?.localizedMessage ?: "",
                        )
                    },
                )
            }
        }
    }

    fun onImportUriSelected(uri: Uri) {
        _uiState.update {
            it.copy(
                pendingImportUri = uri,
                showRestoreConfirmDialog = true,
            )
        }
    }

    fun dismissRestoreConfirmDialog() {
        _uiState.update {
            it.copy(
                pendingImportUri = null,
                showRestoreConfirmDialog = false,
            )
        }
    }

    fun confirmRestoreImport() {
        val uri = _uiState.value.pendingImportUri ?: return
        _uiState.update {
            it.copy(
                showRestoreConfirmDialog = false,
                pendingImportUri = null,
                isProcessing = true,
            )
        }
        viewModelScope.launch {
            val result = backupRepository.importDatabaseFromJsonUri(uri)
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    snackbarMessage = if (result.isSuccess) {
                        UiText.Resource(R.string.settings_import_success)
                    } else {
                        UiText.Resource(
                            R.string.settings_import_failed,
                            result.exceptionOrNull()?.localizedMessage ?: "",
                        )
                    },
                )
            }
        }
    }

    fun snackbarShown() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
