package com.messmanager.app.feature.deposit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.messmanager.app.R
import com.messmanager.app.core.preview.PreviewData
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens
import com.messmanager.app.core.theme.extraColors
import com.messmanager.app.core.util.formatEpochDay
import com.messmanager.app.domain.model.Deposit
import com.messmanager.app.ui.components.AppCard
import com.messmanager.app.ui.components.formatMoney

@Composable
fun DepositItem(
    deposit: Deposit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = deposit.memberName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${stringResource(deposit.method.labelRes)} • ${deposit.epochDay.formatEpochDay()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = formatMoney(deposit.amount),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.extraColors.success,
                maxLines = 1,
            )
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = stringResource(R.string.content_desc_delete),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun DepositItemPreview() {
    PreviewSurface {
        DepositItem(
            deposit = PreviewData.depositItems[0],
            onDelete = {},
            modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
        )
    }
}
