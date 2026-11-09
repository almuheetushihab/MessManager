package com.messmanager.app.feature.members.components

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
import com.messmanager.app.feature.members.MemberFormState
import com.messmanager.app.ui.components.AppCard
import com.messmanager.app.ui.components.AppTextField
import com.messmanager.app.ui.components.FormDialog

@Composable
fun AddMemberDialog(
    form: MemberFormState,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    FormDialog(
        title = stringResource(R.string.members_add_title),
        onConfirm = onSave,
        onDismiss = onDismiss,
    ) {
        AddMemberForm(
            form = form,
            onNameChange = onNameChange,
            onPhoneChange = onPhoneChange,
        )
    }
}

@Composable
fun AddMemberForm(
    form: MemberFormState,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
) {
    AppTextField(
        value = form.name,
        onValueChange = onNameChange,
        label = stringResource(R.string.members_name),
        errorText = form.nameError?.asString(),
    )
    AppTextField(
        value = form.phone,
        onValueChange = onPhoneChange,
        label = stringResource(R.string.members_phone),
        keyboardType = KeyboardType.Phone,
    )
}

@ThemePreviews
@Composable
private fun AddMemberFormPreview() {
    PreviewSurface {
        AppCard(modifier = Modifier.padding(MaterialTheme.dimens.spaceM)) {
            AddMemberForm(
                form = MemberFormState(name = "Mushfiqur Rahim", phone = "01700000000"),
                onNameChange = {},
                onPhoneChange = {},
            )
        }
    }
}
