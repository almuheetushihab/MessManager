package com.messmanager.app.feature.bazar.components

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
import com.messmanager.app.core.util.formatEpochDay
import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.ui.components.AppCard
import com.messmanager.app.ui.components.formatMoney

@Composable
fun BazarItem(
    bazar: Bazar,
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
                    text = stringResource(bazar.category.labelRes),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = bazar.epochDay.formatEpochDay(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
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
    }
}

@ThemePreviews
@Composable
private fun BazarItemWithNotePreview() {
    PreviewSurface {
        BazarItem(
            bazar = PreviewData.bazarItems[0],
            onDelete = {},
            modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
        )
    }
}

@ThemePreviews
@Composable
private fun BazarItemWithoutNotePreview() {
    PreviewSurface {
        BazarItem(
            bazar = PreviewData.bazarItems[0].copy(note = ""),
            onDelete = {},
            modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
        )
    }
}
