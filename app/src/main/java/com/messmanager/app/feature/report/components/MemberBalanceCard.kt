package com.messmanager.app.feature.report.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.messmanager.app.R
import com.messmanager.app.core.preview.PreviewData
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens
import com.messmanager.app.core.theme.extraColors
import com.messmanager.app.core.util.capitalizeWords
import com.messmanager.app.domain.model.MemberSummary
import com.messmanager.app.ui.components.AppCard
import com.messmanager.app.ui.components.MemberAvatar
import com.messmanager.app.ui.components.formatMoney
import kotlin.math.abs

@Composable
fun MemberBalanceCard(
    summary: MemberSummary,
    modifier: Modifier = Modifier,
) {
    val isReceivable = summary.balance > 0.01
    val isPayable = summary.balance < -0.01

    val balanceColor = when {
        isReceivable -> MaterialTheme.extraColors.success
        isPayable -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }

    val balanceLabel = when {
        isReceivable -> R.string.report_balance_receivable
        isPayable -> R.string.report_balance_payable
        else -> R.string.report_status_settled
    }

    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceM),
        ) {
            MemberAvatar(name = summary.member.name)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = summary.member.name.capitalizeWords(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(R.string.report_meals, summary.meals),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
            StatusChip(balance = summary.balance)
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = MaterialTheme.dimens.spaceS))

        if (summary.previousBalance != 0.0) {
            AmountRow(
                label = stringResource(R.string.report_previous_balance),
                amount = formatMoney(summary.previousBalance),
            )
        }
        AmountRow(label = stringResource(R.string.report_meal_cost), amount = formatMoney(summary.mealCost))
        AmountRow(label = stringResource(R.string.report_deposit), amount = formatMoney(summary.deposit))
        if (summary.paidByTotal > 0.0) {
            AmountRow(label = stringResource(R.string.report_paid_by_total), amount = formatMoney(summary.paidByTotal))
        }
        AmountRow(
            label = stringResource(balanceLabel),
            amount = formatMoney(abs(summary.balance)),
            valueColor = balanceColor,
            emphasized = true,
        )
    }
}

@Composable
private fun StatusChip(
    balance: Double,
    modifier: Modifier = Modifier,
) {
    val (labelRes, containerColor, contentColor) = when {
        balance < -0.01 -> Triple(
            R.string.report_status_due,
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
        )
        balance > 0.01 -> Triple(
            R.string.report_status_refund,
            colorResource(R.color.baki_settled_container),
            colorResource(R.color.on_baki_settled_container),
        )
        else -> Triple(
            R.string.report_status_settled,
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    AssistChip(
        onClick = {},
        label = { Text(stringResource(labelRes)) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = containerColor,
            labelColor = contentColor,
        ),
        modifier = modifier,
    )
}

@Composable
private fun AmountRow(
    label: String,
    amount: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    emphasized: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = MaterialTheme.dimens.spaceXs),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = amount,
            style = if (emphasized) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = valueColor,
            maxLines = 1,
        )
    }
}

@ThemePreviews
@Composable
private fun MemberBalanceCardPreview() {
    PreviewSurface {
        Column(
            modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
        ) {
            MemberBalanceCard(summary = PreviewData.memberSummaries[0])
            MemberBalanceCard(summary = PreviewData.memberSummaries[4])
        }
    }
}
