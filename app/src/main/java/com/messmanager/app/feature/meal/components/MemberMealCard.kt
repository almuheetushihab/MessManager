package com.messmanager.app.feature.meal.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.messmanager.app.domain.model.MealEntry
import com.messmanager.app.domain.model.MealType
import com.messmanager.app.feature.meal.MemberMealUi
import com.messmanager.app.ui.components.AppCard

@Composable
fun MemberMealCard(
    item: MemberMealUi,
    onToggle: (MealEntry, MealType) -> Unit,
    onGuestsChange: (MealEntry, Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val entry = item.entry
    val isAllMealsOn = entry.breakfast && entry.lunch && entry.dinner

    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.member.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(R.string.meal_member_day_total, entry.totalCount),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (isAllMealsOn) {
                AssistChip(
                    onClick = {},
                    label = { Text(stringResource(R.string.meal_all_meals_on)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = colorResource(R.color.all_on_chip_container),
                        labelColor = colorResource(R.color.on_all_on_chip_container),
                        leadingIconContentColor = colorResource(R.color.on_all_on_chip_container),
                    ),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = MaterialTheme.dimens.spaceS),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
        ) {
            MealType.entries.forEach { type ->
                FilterChip(
                    selected = entry.isOn(type),
                    onClick = { onToggle(entry, type) },
                    enabled = enabled,
                    label = { Text(stringResource(type.labelRes)) },
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.meal_guest),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onGuestsChange(entry, -1) }, enabled = enabled && entry.guests > 0) {
                    Icon(Icons.Rounded.Remove, contentDescription = stringResource(R.string.content_desc_decrease))
                }
                Text(text = entry.guests.toString(), style = MaterialTheme.typography.titleMedium, maxLines = 1)
                IconButton(onClick = { onGuestsChange(entry, 1) }, enabled = enabled) {
                    Icon(Icons.Rounded.Add, contentDescription = stringResource(R.string.content_desc_increase))
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun MemberMealCardAllOffPreview() {
    PreviewSurface {
        val ui = PreviewData.memberMealUis[4]
        MemberMealCard(
            item = ui,
            onToggle = { _, _ -> },
            onGuestsChange = { _, _ -> },
            modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
        )
    }
}

@ThemePreviews
@Composable
private fun MemberMealCardAllOnPreview() {
    PreviewSurface {
        val ui = PreviewData.memberMealUis[0].copy(
            entry = PreviewData.memberMealUis[0].entry.copy(breakfast = true, lunch = true, dinner = true),
        )
        MemberMealCard(
            item = ui,
            onToggle = { _, _ -> },
            onGuestsChange = { _, _ -> },
            modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
        )
    }
}

@ThemePreviews
@Composable
private fun MemberMealCardWithGuestsPreview() {
    PreviewSurface {
        val ui = PreviewData.memberMealUis[2]
        MemberMealCard(
            item = ui,
            onToggle = { _, _ -> },
            onGuestsChange = { _, _ -> },
            modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
        )
    }
}
