package com.messmanager.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.messmanager.app.R
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens

@Composable
fun FormDialog(
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    confirmText: String = stringResource(R.string.action_save),
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS)) {
                content()
            }
        },
        confirmButton = { AppTextButton(text = confirmText, onClick = onConfirm) },
        dismissButton = { AppTextButton(text = stringResource(R.string.action_cancel), onClick = onDismiss) },
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
    )
}

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    confirmText: String = stringResource(R.string.action_delete),
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
        text = { Text(text = message, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Start) },
        confirmButton = { AppTextButton(text = confirmText, onClick = onConfirm) },
        dismissButton = { AppTextButton(text = stringResource(R.string.action_cancel), onClick = onDismiss) },
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
    )
}

@ThemePreviews
@Composable
private fun DialogsPreview() {
    PreviewSurface {
        Column(
            modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
        ) {
            FormDialog(
                title = stringResource(R.string.sample_dialog_title),
                onConfirm = {},
                onDismiss = {},
            ) {
                Text(stringResource(R.string.sample_dialog_content))
            }
            ConfirmDialog(
                title = stringResource(R.string.sample_delete_title),
                message = stringResource(R.string.sample_delete_message),
                onConfirm = {},
                onDismiss = {},
            )
        }
    }
}
