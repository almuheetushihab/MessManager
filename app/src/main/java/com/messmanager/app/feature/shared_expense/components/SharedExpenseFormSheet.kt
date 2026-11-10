package com.messmanager.app.feature.shared_expense.components

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
import com.messmanager.app.core.theme.dimens
import com.messmanager.app.core.util.capitalizeWords
import com.messmanager.app.core.util.formatEpochDay
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.SharedExpenseType
import com.messmanager.app.domain.model.SplitType
import com.messmanager.app.feature.shared_expense.SharedExpenseFormState
import com.messmanager.app.ui.components.AppCard
import com.messmanager.app.ui.components.AppDropdownField
import com.messmanager.app.ui.components.AppTextField
import com.messmanager.app.ui.components.formatMoney
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedExpenseFormSheet(
    form: SharedExpenseFormState,
    members: List<Member>,
    onTitleChange: (String) -> Unit,
    onTypeChange: (SharedExpenseType) -> Unit,
    onAmountChange: (String) -> Unit,
    onSplitTypeChange: (SplitType) -> Unit,
    onPaidByMemberChange: (Long?) -> Unit,
    onDateChange: (Long) -> Unit,
    onCustomShareChange: (memberId: Long, shareText: String) -> Unit,
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
        SharedExpenseFormSheetContent(
            form = form,
            members = members,
            onTitleChange = onTitleChange,
            onTypeChange = onTypeChange,
            onAmountChange = onAmountChange,
            onSplitTypeChange = onSplitTypeChange,
            onPaidByMemberChange = onPaidByMemberChange,
            onDateChange = onDateChange,
            onCustomShareChange = onCustomShareChange,
            onSave = onSave,
            onDismiss = onDismiss,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedExpenseFormSheetContent(
    form: SharedExpenseFormState,
    members: List<Member>,
    onTitleChange: (String) -> Unit,
    onTypeChange: (SharedExpenseType) -> Unit,
    onAmountChange: (String) -> Unit,
    onSplitTypeChange: (SplitType) -> Unit,
    onPaidByMemberChange: (Long?) -> Unit,
    onDateChange: (Long) -> Unit,
    onCustomShareChange: (memberId: Long, shareText: String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val titleFocusRequester = remember { FocusRequester() }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        titleFocusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaterialTheme.dimens.spaceM, vertical = MaterialTheme.dimens.spaceS),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceM),
    ) {
        Text(
            text = stringResource(if (form.isEdit) R.string.shared_expense_edit_title else R.string.shared_expense_add_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        // Title Field
        AppTextField(
            value = form.title,
            onValueChange = onTitleChange,
            label = stringResource(R.string.shared_expense_expense_title),
            imeAction = ImeAction.Next,
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            focusRequester = titleFocusRequester,
            errorText = if (form.titleError != null) stringResource(R.string.error_title_required) else null,
        )

        // Category / Type Chips
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceXs)) {
            Text(
                text = stringResource(R.string.shared_expense_type),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceXs),
            ) {
                SharedExpenseType.entries.forEach { type ->
                    FilterChip(
                        selected = form.type == type,
                        onClick = { onTypeChange(type) },
                        label = { Text(stringResource(type.labelRes)) },
                    )
                }
            }
        }

        // Amount Field
        AppTextField(
            value = form.amountText,
            onValueChange = onAmountChange,
            label = stringResource(R.string.bazar_amount),
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Next,
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            errorText = if (form.amountError != null) stringResource(R.string.error_invalid_amount) else null,
        )

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
            value = form.epochDay.formatEpochDay(),
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

        // Split Type Segmented Chips
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceXs)) {
            Text(
                text = stringResource(R.string.shared_expense_split_type),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS)) {
                SplitType.entries.forEach { splitType ->
                    FilterChip(
                        selected = form.splitType == splitType,
                        onClick = { onSplitTypeChange(splitType) },
                        label = { Text(stringResource(splitType.labelRes)) },
                    )
                }
            }
        }

        // Split details: EQUAL vs CUSTOM
        if (form.splitType == SplitType.EQUAL) {
            val total = form.amountText.toDoubleOrNull() ?: 0.0
            val count = members.size
            val perMemberShare = if (count > 0) total / count else 0.0
            AppCard {
                Text(
                    text = stringResource(
                        R.string.shared_expense_equal_split_info,
                        formatMoney(perMemberShare),
                        count,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            // CUSTOM split fields
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS)) {
                form.customShares.forEach { shareInput ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
                    ) {
                        Text(
                            text = shareInput.memberName.capitalizeWords(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f),
                        )
                        AppTextField(
                            value = shareInput.amountText,
                            onValueChange = { onCustomShareChange(shareInput.memberId, it) },
                            label = stringResource(R.string.bazar_amount),
                            keyboardType = KeyboardType.Decimal,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                if (form.sharesError != null) {
                    val currentTotal = form.customShares.sumOf { it.amountText.toDoubleOrNull() ?: 0.0 }
                    val targetTotal = form.amountText.toDoubleOrNull() ?: 0.0
                    Text(
                        text = stringResource(
                            R.string.shared_expense_custom_split_error,
                            formatMoney(currentTotal),
                            formatMoney(targetTotal),
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }

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
                Text(stringResource(if (form.isEdit) R.string.action_save else R.string.action_add))
            }
        }
    }

    if (showDatePicker) {
        val initialMillis = LocalDate.ofEpochDay(form.epochDay)
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
