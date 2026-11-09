package com.messmanager.app.feature.bazar.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.messmanager.app.R
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens
import com.messmanager.app.domain.model.BazarCategory
import com.messmanager.app.feature.bazar.BazarFormState
import com.messmanager.app.ui.components.AppCard
import com.messmanager.app.ui.components.AppDropdownField
import com.messmanager.app.ui.components.AppTextField
import com.messmanager.app.ui.components.FormDialog

@Composable
fun AddBazarDialog(
    form: BazarFormState,
    onAmountChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onCategoryChange: (BazarCategory) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    FormDialog(
        title = stringResource(R.string.bazar_add_title),
        onConfirm = onSave,
        onDismiss = onDismiss,
    ) {
        AddBazarForm(
            form = form,
            onAmountChange = onAmountChange,
            onNoteChange = onNoteChange,
            onCategoryChange = onCategoryChange,
        )
    }
}

@Composable
fun AddBazarForm(
    form: BazarFormState,
    onAmountChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onCategoryChange: (BazarCategory) -> Unit,
) {
    AppTextField(
        value = form.amount,
        onValueChange = onAmountChange,
        label = stringResource(R.string.bazar_amount),
        keyboardType = KeyboardType.Decimal,
        errorText = form.amountError?.asString(),
    )
    AppDropdownField(
        label = stringResource(R.string.bazar_category),
        selectedText = stringResource(form.category.labelRes),
        options = BazarCategory.entries.map { stringResource(it.labelRes) to it },
        onSelected = onCategoryChange,
    )
    AppTextField(
        value = form.note,
        onValueChange = onNoteChange,
        label = stringResource(R.string.bazar_note),
    )
}

@ThemePreviews
@Composable
private fun AddBazarFormPreview() {
    PreviewSurface {
        AppCard(modifier = Modifier.padding(MaterialTheme.dimens.spaceM)) {
            AddBazarForm(
                form = BazarFormState(amount = "500", note = "Fresh vegetables", category = BazarCategory.VEGETABLE),
                onAmountChange = {},
                onNoteChange = {},
                onCategoryChange = {},
            )
        }
    }
}
