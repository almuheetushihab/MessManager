package com.messmanager.app.feature.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.messmanager.app.R
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens
import com.messmanager.app.data.repository.AppLanguage
import com.messmanager.app.data.repository.AppTheme
import com.messmanager.app.data.repository.UserSettings
import com.messmanager.app.ui.components.AppCard
import com.messmanager.app.ui.components.AppTextField
import com.messmanager.app.ui.components.AppTopBar
import com.messmanager.app.ui.components.SectionTitle
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        uri?.let { viewModel.exportDatabase(it) }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let { viewModel.onImportUriSelected(it) }
    }

    val snackbarMsg = state.snackbarMessage
    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it.asString(context))
            viewModel.snackbarShown()
        }
    }

    SettingsContent(
        state = state,
        onBack = onBack,
        onThemeSelected = viewModel::setThemeConfig,
        onLanguageSelected = viewModel::setLanguageCode,
        onMessNameChanged = viewModel::setMessName,
        onCutoffTimeChanged = viewModel::setCutoffTime,
        onAdminOverrideChanged = viewModel::setAdminOverride,
        onExportClick = {
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"))
            exportLauncher.launch("mess_manager_backup_$timestamp.json")
        },
        onImportClick = {
            importLauncher.launch(arrayOf("application/json", "*/*"))
        },
        onConfirmRestore = viewModel::confirmRestoreImport,
        onDismissRestore = viewModel::dismissRestoreConfirmDialog,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun SettingsContent(
    state: SettingsUiState,
    onBack: () -> Unit,
    onThemeSelected: (AppTheme) -> Unit,
    onLanguageSelected: (String) -> Unit,
    onMessNameChanged: (String) -> Unit,
    onCutoffTimeChanged: (String?) -> Unit,
    onAdminOverrideChanged: (Boolean) -> Unit,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    onConfirmRestore: () -> Unit,
    onDismissRestore: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = stringResource(R.string.settings_title),
                onBack = onBack,
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.dimens.spaceM),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceM),
        ) {
            // Section 1: Appearance & Language
            AppCard {
                Column(
                    modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
                ) {
                    SectionTitle(text = stringResource(R.string.settings_appearance))

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(R.string.settings_theme),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Column(Modifier.selectableGroup()) {
                        ThemeOptionRow(
                            label = stringResource(R.string.settings_theme_system),
                            selected = state.settings.themeConfig == AppTheme.SYSTEM,
                            onClick = { onThemeSelected(AppTheme.SYSTEM) },
                        )
                        ThemeOptionRow(
                            label = stringResource(R.string.settings_theme_light),
                            selected = state.settings.themeConfig == AppTheme.LIGHT,
                            onClick = { onThemeSelected(AppTheme.LIGHT) },
                        )
                        ThemeOptionRow(
                            label = stringResource(R.string.settings_theme_dark),
                            selected = state.settings.themeConfig == AppTheme.DARK,
                            onClick = { onThemeSelected(AppTheme.DARK) },
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    SectionTitle(text = stringResource(R.string.settings_language))

                    Column(Modifier.selectableGroup()) {
                        ThemeOptionRow(
                            label = stringResource(R.string.settings_lang_system),
                            selected = state.settings.languageCode == AppLanguage.SYSTEM.code,
                            onClick = { onLanguageSelected(AppLanguage.SYSTEM.code) },
                        )
                        ThemeOptionRow(
                            label = stringResource(R.string.settings_lang_en),
                            selected = state.settings.languageCode == AppLanguage.ENGLISH.code,
                            onClick = { onLanguageSelected(AppLanguage.ENGLISH.code) },
                        )
                        ThemeOptionRow(
                            label = stringResource(R.string.settings_lang_bn),
                            selected = state.settings.languageCode == AppLanguage.BANGLA.code,
                            onClick = { onLanguageSelected(AppLanguage.BANGLA.code) },
                        )
                    }
                }
            }

            // Section 2: Mess Info
            AppCard {
                Column(
                    modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
                ) {
                    SectionTitle(text = stringResource(R.string.settings_mess_info))

                    AppTextField(
                        value = state.settings.messName,
                        onValueChange = onMessNameChanged,
                        label = stringResource(R.string.settings_mess_name),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            // Section 3: Meal Rules
            AppCard {
                Column(
                    modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
                ) {
                    SectionTitle(text = stringResource(R.string.settings_meal_rules))

                    AppTextField(
                        value = state.settings.cutoffTime.orEmpty(),
                        onValueChange = { onCutoffTimeChanged(it.ifBlank { null }) },
                        label = stringResource(R.string.settings_cutoff_time),
                        helperText = stringResource(R.string.settings_cutoff_time_helper),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.settings_admin_override),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = stringResource(R.string.settings_admin_override_summary),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(
                            checked = state.settings.adminOverride,
                            onCheckedChange = onAdminOverrideChanged,
                        )
                    }
                }
            }

            // Section 4: Backup & Restore
            AppCard {
                Column(
                    modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
                ) {
                    SectionTitle(text = stringResource(R.string.settings_backup_restore))

                    Text(
                        text = stringResource(R.string.settings_backup_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = onExportClick,
                        enabled = !state.isProcessing,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Download,
                            contentDescription = stringResource(R.string.settings_export_db),
                        )
                        Spacer(modifier = Modifier.padding(end = MaterialTheme.dimens.spaceS))
                        Text(text = stringResource(R.string.settings_export_db))
                    }

                    OutlinedButton(
                        onClick = onImportClick,
                        enabled = !state.isProcessing,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Backup,
                            contentDescription = stringResource(R.string.settings_import_db),
                        )
                        Spacer(modifier = Modifier.padding(end = MaterialTheme.dimens.spaceS))
                        Text(text = stringResource(R.string.settings_import_db))
                    }
                }
            }
        }

        if (state.showRestoreConfirmDialog) {
            AlertDialog(
                onDismissRequest = onDismissRestore,
                title = { Text(text = stringResource(R.string.settings_restore_confirm_title)) },
                text = { Text(text = stringResource(R.string.settings_restore_confirm_msg)) },
                confirmButton = {
                    TextButton(onClick = onConfirmRestore) {
                        Text(
                            text = stringResource(R.string.action_save),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissRestore) {
                        Text(text = stringResource(R.string.action_cancel))
                    }
                },
            )
        }
    }
}

@Composable
private fun ThemeOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(vertical = MaterialTheme.dimens.spaceS),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = MaterialTheme.dimens.spaceS),
        )
    }
}

@ThemePreviews
@Composable
private fun SettingsScreenPreview() {
    PreviewSurface {
        SettingsContent(
            state = SettingsUiState(settings = UserSettings(messName = "Green Mess")),
            onBack = {},
            onThemeSelected = {},
            onLanguageSelected = {},
            onMessNameChanged = {},
            onCutoffTimeChanged = {},
            onAdminOverrideChanged = {},
            onExportClick = {},
            onImportClick = {},
            onConfirmRestore = {},
            onDismissRestore = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}
