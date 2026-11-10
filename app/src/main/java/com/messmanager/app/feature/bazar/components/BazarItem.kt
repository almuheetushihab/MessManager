package com.messmanager.app.feature.bazar.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.LocalGasStation
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.messmanager.app.R
import com.messmanager.app.core.preview.PreviewData
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens
import com.messmanager.app.core.util.formatEpochDay
import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.BazarCategory
import com.messmanager.app.domain.model.PaymentType
import com.messmanager.app.ui.components.AppCard
import com.messmanager.app.ui.components.formatMoney

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BazarItem(
    bazar: Bazar,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMarkAsPaid: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onEdit() },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
            ) {
                Icon(
                    imageVector = getCategoryIcon(bazar.category),
                    contentDescription = stringResource(bazar.category.labelRes),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(MaterialTheme.dimens.iconM),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(bazar.category.labelRes),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val paidByText = bazar.paidByMemberName?.let {
                        stringResource(R.string.bazar_paid_by_member, it)
                    } ?: stringResource(R.string.bazar_paid_by_mess_fund)

                    Text(
                        text = "$paidByText • ${bazar.epochDay.formatEpochDay()}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = formatMoney(bazar.amount),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
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

            // Chips Row: Quantity + Unit, Payment Type (Cash/Baki)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceXs),
            ) {
                if (bazar.quantity != null && bazar.unit != null) {
                    val qtyFormatted = if (bazar.quantity % 1.0 == 0.0) {
                        bazar.quantity.toLong().toString()
                    } else {
                        bazar.quantity.toString()
                    }
                    AssistChip(
                        onClick = {},
                        label = { Text("$qtyFormatted ${stringResource(bazar.unit.symbolRes)}") },
                    )
                }

                when (bazar.paymentType) {
                    PaymentType.CASH -> {
                        AssistChip(
                            onClick = {},
                            label = { Text(stringResource(R.string.payment_type_cash)) },
                        )
                    }
                    PaymentType.CREDIT -> {
                        if (bazar.isCreditSettled) {
                            AssistChip(
                                onClick = {},
                                label = { Text(stringResource(R.string.bazar_settled)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(AssistChipDefaults.IconSize),
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = colorResource(R.color.baki_settled_container),
                                    labelColor = colorResource(R.color.on_baki_settled_container),
                                    leadingIconContentColor = colorResource(R.color.on_baki_settled_container),
                                ),
                            )
                        } else {
                            val labelText = if (bazar.creditShopName.isNotBlank()) {
                                "${stringResource(R.string.payment_type_baki)} (${bazar.creditShopName})"
                            } else {
                                stringResource(R.string.payment_type_baki)
                            }
                            AssistChip(
                                onClick = {},
                                label = { Text(labelText) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = colorResource(R.color.baki_container),
                                    labelColor = colorResource(R.color.on_baki_container),
                                ),
                            )
                        }
                    }
                }
            }

            // Mark as paid action for unsettled Baki items
            if (bazar.paymentType == PaymentType.CREDIT && !bazar.isCreditSettled) {
                OutlinedButton(
                    onClick = onMarkAsPaid,
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text(stringResource(R.string.bazar_mark_as_paid))
                }
            }

            if (bazar.note.isNotBlank()) {
                Text(
                    text = bazar.note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private fun getCategoryIcon(category: BazarCategory): ImageVector = when (category) {
    BazarCategory.FISH_MEAT -> Icons.Rounded.Restaurant
    BazarCategory.VEGETABLE -> Icons.Rounded.LocalGroceryStore
    BazarCategory.GROCERY -> Icons.Rounded.ShoppingCart
    BazarCategory.GAS -> Icons.Rounded.LocalGasStation
    BazarCategory.OTHER -> Icons.Rounded.Category
}

@ThemePreviews
@Composable
private fun BazarItemCashPreview() {
    PreviewSurface {
        BazarItem(
            bazar = PreviewData.bazarItems[0],
            onEdit = {},
            onDelete = {},
            onMarkAsPaid = {},
            modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
        )
    }
}

@ThemePreviews
@Composable
private fun BazarItemBakiUnsettledPreview() {
    PreviewSurface {
        BazarItem(
            bazar = PreviewData.bazarItems[1],
            onEdit = {},
            onDelete = {},
            onMarkAsPaid = {},
            modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
        )
    }
}
