package com.messmanager.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.messmanager.app.R
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> AppDropdownField(
    label: String,
    selectedText: String,
    options: List<Pair<String, T>>,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    errorText: String? = null,
    helperText: String? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    val supportingTextContent: @Composable (() -> Unit)? = when {
        errorText != null -> {
            { Text(errorText) }
        }
        helperText != null -> {
            { Text(helperText, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        else -> null
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            isError = errorText != null,
            supportingText = supportingTextContent,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (text, value) ->
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = {
                        onSelected(value)
                        expanded = false
                    },
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun AppDropdownFieldPreview() {
    PreviewSurface {
        val groceryText = stringResource(R.string.category_grocery)
        val fishMeatText = stringResource(R.string.category_fish_meat)
        AppDropdownField(
            label = stringResource(R.string.sample_category),
            selectedText = groceryText,
            options = listOf(groceryText to 1, fishMeatText to 2),
            onSelected = {},
            modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
        )
    }
}
