package com.messmanager.app.feature.bazar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.messmanager.app.R
import com.messmanager.app.core.preview.PreviewData
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens
import com.messmanager.app.core.util.formatEpochDay
import com.messmanager.app.domain.model.BazarCategory
import com.messmanager.app.domain.model.BazarUnit
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.PaymentType
import com.messmanager.app.feature.bazar.BazarFormState
import com.messmanager.app.ui.components.AppDropdownField
import com.messmanager.app.ui.components.AppTextField
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BazarFormSheet(
    form: BazarFormState,
    members: List<Member>,
    onAmountChange: (String) -> Unit,
    onQuickAmountClick: (Int) -> Unit,
    onCategoryChange: (BazarCategory) -> Unit,
    onQuantityChange: (String) -> Unit,
    onUnitChange: (BazarUnit) -> Unit,
    onPaymentTypeChange: (PaymentType) -> Unit,
    onCreditShopNameChange: (String) -> Unit,
    onPaidByMemberChange: (Long?) -> Unit,
    onDateChange: (Long) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier,
    ) {
        BazarFormSheetContent(
            form = form,
            members = members,
            onAmountChange = onAmountChange,
            onQuickAmountClick = onQuickAmountClick,
            onCategoryChange = onCategoryChange,
            onQuantityChange = onQuantityChange,
            onUnitChange = onUnitChange,
            onPaymentTypeChange = onPaymentTypeChange,
            onCreditShopNameChange = onCreditShopNameChange,
            onPaidByMemberChange = onPaidByMemberChange,
            onDateChange = onDateChange,
            onNoteChange = onNoteChange,
            onSave = onSave,
            onDismiss = onDismiss,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BazarFormSheetContent(
    form: BazarFormState,
    members: List<Member>,
    onAmountChange: (String) -> Unit,
    onQuickAmountClick: (Int) -> Unit,
    onCategoryChange: (BazarCategory) -> Unit,
    onQuantityChange: (String) -> Unit,
    onUnitChange: (BazarUnit) -> Unit,
    onPaymentTypeChange: (PaymentType) -> Unit,
    onCreditShopNameChange: (String) -> Unit,
    onPaidByMemberChange: (Long?) -> Unit,
    onDateChange: (Long) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val amountFocusRequester = remember { FocusRequester() }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        amountFocusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaterialTheme.dimens.spaceM, vertical = MaterialTheme.dimens.spaceS),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceM),
    ) {
        Text(
            text = stringResource(if (form.isEditMode) R.string.bazar_edit_title else R.string.bazar_add_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        // Amount Field & Quick Chips
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceXs)) {
            AppTextField(
                value = form.amount,
                onValueChange = onAmountChange,
                label = stringResource(R.string.bazar_amount),
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                focusRequester = amountFocusRequester,
                errorText = form.amountError?.asString(),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceXs),
                modifier = Modifier.fillMaxWidth(),
            ) {
                listOf(50, 100, 200, 500, 1000).forEach { chipAmount ->
                    SuggestionChip(
                        onClick = { onQuickAmountClick(chipAmount) },
                        label = { Text(stringResource(R.string.quick_amount_format, chipAmount)) },
                    )
                }
            }
        }

        // Category Dropdown
        AppDropdownField(
            label = stringResource(R.string.bazar_category),
            selectedText = stringResource(form.category.labelRes),
            options = BazarCategory.entries.map { stringResource(it.labelRes) to it },
            onSelected = onCategoryChange,
        )

        // Quantity + Unit Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
            modifier = Modifier.fillMaxWidth(),
        ) {
            AppTextField(
                value = form.quantity,
                onValueChange = onQuantityChange,
                label = stringResource(R.string.bazar_quantity),
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Right) }),
                modifier = Modifier.weight(1f),
            )
            AppDropdownField(
                label = stringResource(R.string.bazar_unit),
                selectedText = stringResource(form.unit.labelRes),
                options = form.allowedUnits.map { stringResource(it.labelRes) to it },
                onSelected = onUnitChange,
                modifier = Modifier.weight(1f),
            )
        }

        // Cash / Baki Segmented Toggle
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceXs)) {
            Text(
                text = stringResource(R.string.bazar_payment_type),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS)) {
                PaymentType.entries.forEach { type ->
                    FilterChip(
                        selected = form.paymentType == type,
                        onClick = { onPaymentTypeChange(type) },
                        label = { Text(stringResource(type.labelRes)) },
                    )
                }
            }
        }

        // Shop Name if Baki
        if (form.paymentType == PaymentType.CREDIT) {
            AppTextField(
                value = form.creditShopName,
                onValueChange = onCreditShopNameChange,
                label = stringResource(R.string.bazar_credit_shop_name),
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                errorText = form.shopNameError?.asString(),
            )
        }

        // Paid By Dropdown
        val paidBySelectedText = form.paidByMemberId?.let { id ->
            members.find { it.id == id }?.name
        } ?: stringResource(R.string.bazar_paid_by_mess_fund)

        AppDropdownField(
            label = stringResource(R.string.bazar_paid_by),
            selectedText = paidBySelectedText,
            options = listOf(stringResource(R.string.bazar_paid_by_mess_fund) to null) +
                members.map { it.name to (it.id as Long?) },
            onSelected = onPaidByMemberChange,
            helperText = stringResource(R.string.bazar_paid_by_helper),
        )

        // Date Picker Field
        OutlinedTextField(
            value = form.dateEpochDay.formatEpochDay(),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.bazar_date)) },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Rounded.CalendarToday,
                        contentDescription = stringResource(R.string.bazar_date),
                    )
                }
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
        )

        // Note Field
        AppTextField(
            value = form.note,
            onValueChange = onNoteChange,
            label = stringResource(R.string.bazar_note),
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                onSave()
            }),
        )

        // Action Buttons Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaterialTheme.dimens.spaceS, bottom = MaterialTheme.dimens.spaceM),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS, Alignment.End),
        ) {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
            Button(onClick = onSave) {
                Text(stringResource(if (form.isEditMode) R.string.action_save else R.string.action_add))
            }
        }
    }

    if (showDatePicker) {
        val initialMillis = LocalDate.ofEpochDay(form.dateEpochDay)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()

        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                        onDateChange(selectedDate.toEpochDay())
                    }
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.action_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@ThemePreviews
@Composable
private fun BazarFormSheetAddPreview() {
    PreviewSurface {
        BazarFormSheetContent(
            form = BazarFormState(),
            members = PreviewData.members,
            onAmountChange = {},
            onQuickAmountClick = {},
            onCategoryChange = {},
            onQuantityChange = {},
            onUnitChange = {},
            onPaymentTypeChange = {},
            onCreditShopNameChange = {},
            onPaidByMemberChange = {},
            onDateChange = {},
            onNoteChange = {},
            onSave = {},
            onDismiss = {},
        )
    }
}

@ThemePreviews
@Composable
private fun BazarFormSheetEditPreview() {
    PreviewSurface {
        BazarFormSheetContent(
            form = BazarFormState(
                id = 1L,
                amount = "1250",
                quantity = "2.5",
                category = BazarCategory.FISH_MEAT,
                unit = BazarUnit.KG,
                paymentType = PaymentType.CREDIT,
                creditShopName = "Bhai Bhai Fish Corner",
                paidByMemberId = PreviewData.members[0].id,
                note = "Hilsha & Chicken",
            ),
            members = PreviewData.members,
            onAmountChange = {},
            onQuickAmountClick = {},
            onCategoryChange = {},
            onQuantityChange = {},
            onUnitChange = {},
            onPaymentTypeChange = {},
            onCreditShopNameChange = {},
            onPaidByMemberChange = {},
            onDateChange = {},
            onNoteChange = {},
            onSave = {},
            onDismiss = {},
        )
    }
}
