package com.messmanager.app.feature.meal.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.messmanager.app.R
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens
import com.messmanager.app.domain.model.MealType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MealQuickActions(
    onAllOn: (MealType) -> Unit,
    onCopyYesterday: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceXs),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceXs),
    ) {
        MealType.entries.forEach { type ->
            val labelRes = when (type) {
                MealType.BREAKFAST -> R.string.meal_all_bf_on
                MealType.LUNCH -> R.string.meal_all_lunch_on
                MealType.DINNER -> R.string.meal_all_dinner_on
            }
            AssistChip(
                onClick = { onAllOn(type) },
                enabled = enabled,
                label = { Text(stringResource(labelRes)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.DoneAll,
                        contentDescription = null,
                    )
                },
            )
        }
        AssistChip(
            onClick = onCopyYesterday,
            enabled = enabled,
            label = { Text(stringResource(R.string.meal_copy_yesterday)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.ContentCopy,
                    contentDescription = null,
                )
            },
        )
    }
}

@ThemePreviews
@Composable
private fun MealQuickActionsPreview() {
    PreviewSurface {
        MealQuickActions(
            onAllOn = {},
            onCopyYesterday = {},
            modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
        )
    }
}
