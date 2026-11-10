package com.messmanager.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester? = null,
    errorText: String? = null,
    helperText: String? = null,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val fieldModifier = modifier
        .fillMaxWidth()
        .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
        .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)

    val supportingTextContent: @Composable (() -> Unit)? = when {
        errorText != null -> {
            { Text(errorText) }
        }
        helperText != null -> {
            { Text(helperText, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        else -> null
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = fieldModifier,
        label = { Text(label) },
        singleLine = true,
        readOnly = readOnly || (onClick != null),
        enabled = onClick == null,
        isError = errorText != null,
        supportingText = supportingTextContent,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = keyboardActions,
        trailingIcon = trailingIcon,
        shape = MaterialTheme.shapes.small,
    )
}

@ThemePreviews
@Composable
private fun AppTextFieldPreviews() {
    PreviewSurface {
        Column(
            modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceM),
        ) {
            AppTextField(value = "", onValueChange = {}, label = "Normal Field")
            AppTextField(value = "Rahim Uddin", onValueChange = {}, label = "Filled Field")
            AppTextField(value = "-50", onValueChange = {}, label = "Error Field", errorText = "Enter a valid amount")
        }
    }
}
