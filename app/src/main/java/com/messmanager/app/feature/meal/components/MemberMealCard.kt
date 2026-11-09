package com.messmanager.app.feature.meal.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
) {
    val entry = item.entry
    AppCard(modifier = modifier) {
        Text(text = item.member.name, style = MaterialTheme.typography.titleMedium)
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
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onGuestsChange(entry, -1) }, enabled = entry.guests > 0) {
                    Icon(Icons.Rounded.Remove, contentDescription = stringResource(R.string.content_desc_decrease))
                }
                Text(text = entry.guests.toString(), style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = { onGuestsChange(entry, 1) }) {
                    Icon(Icons.Rounded.Add, contentDescription = stringResource(R.string.content_desc_increase))
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun MemberMealCardPreview() {
    PreviewSurface {
        MemberMealCard(
            item = PreviewData.memberMealUis[0],
            onToggle = { _, _ -> },
            onGuestsChange = { _, _ -> },
            modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
        )
    }
}
