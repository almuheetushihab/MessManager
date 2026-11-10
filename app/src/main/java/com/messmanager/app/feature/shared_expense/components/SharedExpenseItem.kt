package com.messmanager.app.feature.shared_expense.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalGasStation
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.messmanager.app.R
import com.messmanager.app.core.preview.PreviewData
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens
import com.messmanager.app.core.util.capitalizeWords
import com.messmanager.app.core.util.formatEpochDay
import com.messmanager.app.domain.model.SharedExpense
import com.messmanager.app.domain.model.SharedExpenseType
import com.messmanager.app.ui.components.AppCard
import com.messmanager.app.ui.components.formatMoney

@Composable
fun SharedExpenseItem(
    expense: SharedExpense,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showMenu by remember { mutableStateOf(false) }

    val icon: ImageVector = when (expense.type) {
        SharedExpenseType.GAS -> Icons.Rounded.LocalGasStation
        SharedExpenseType.WIFI -> Icons.Rounded.Wifi
        SharedExpenseType.ELECTRICITY -> Icons.Rounded.Bolt
        SharedExpenseType.MAID -> Icons.Rounded.CleaningServices
        SharedExpenseType.RENT -> Icons.Rounded.Home
        SharedExpenseType.OTHER -> Icons.AutoMirrored.Rounded.ReceiptLong
    }

    val splitText = stringResource(expense.splitType.labelRes)
    val paidByText = if (!expense.paidByMemberName.isNullOrBlank()) {
        stringResource(R.string.bazar_paid_by_member, expense.paidByMemberName.capitalizeWords())
    } else {
        stringResource(R.string.bazar_paid_by_mess_fund)
    }

    val subtitleText = "$splitText • $paidByText • ${expense.epochDay.formatEpochDay()}"

    AppCard(
        modifier = modifier.clickable(onClick = onEdit),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceM),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(expense.type.labelRes),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.title.capitalizeWords(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitleText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = formatMoney(expense.amount),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
            )
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = stringResource(R.string.content_desc_options),
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.bazar_edit_title)) },
                    onClick = {
                        showMenu = false
                        onEdit()
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Rounded.Edit, contentDescription = stringResource(R.string.content_desc_edit))
                    },
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.action_delete)) },
                    onClick = {
                        showMenu = false
                        onDelete()
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Rounded.Delete, contentDescription = stringResource(R.string.content_desc_delete))
                    },
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun SharedExpenseItemPreview() {
    PreviewSurface {
        SharedExpenseItem(
            expense = PreviewData.sharedExpenses[0],
            onEdit = {},
            onDelete = {},
            modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
        )
    }
}
