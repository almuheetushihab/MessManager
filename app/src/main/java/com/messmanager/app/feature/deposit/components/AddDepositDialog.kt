package com.messmanager.app.feature.deposit.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.messmanager.app.R
import com.messmanager.app.core.preview.PreviewData
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.PaymentMethod
import com.messmanager.app.feature.deposit.DepositFormState
import com.messmanager.app.ui.components.AppDropdownField
import com.messmanager.app.ui.components.AppTextField
import com.messmanager.app.ui.components.FormDialog

@Composable
fun AddDepositDialog(
    form: DepositFormState,
    members: List<Member>,
    onMemberChange: (Member) -> Unit,
    onAmountChange: (String) -> Unit,
    onMethodChange: (PaymentMethod) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    FormDialog(
        title = stringResource(R.string.deposit_add_title),
        onConfirm = onSave,
        onDismiss = onDismiss,
    ) {
        AppDropdownField(
            label = stringResource(R.string.deposit_member),
            selectedText = form.member?.name.orEmpty(),
            options = members.map { it.name to it },
            onSelected = onMemberChange,
            errorText = form.memberError?.asString(),
        )
        AppTextField(
            value = form.amount,
            onValueChange = onAmountChange,
            label = stringResource(R.string.deposit_amount),
            keyboardType = KeyboardType.Decimal,
            errorText = form.amountError?.asString(),
        )
        AppDropdownField(
            label = stringResource(R.string.deposit_method),
            selectedText = stringResource(form.method.labelRes),
            options = PaymentMethod.entries.map { stringResource(it.labelRes) to it },
            onSelected = onMethodChange,
        )
    }
}

@ThemePreviews
@Composable
private fun AddDepositDialogPreview() {
    PreviewSurface {
        AddDepositDialog(
            form = DepositFormState(member = PreviewData.members[0], amount = "1000", method = PaymentMethod.BKASH),
            members = PreviewData.members,
            onMemberChange = {},
            onAmountChange = {},
            onMethodChange = {},
            onSave = {},
            onDismiss = {},
        )
    }
}
