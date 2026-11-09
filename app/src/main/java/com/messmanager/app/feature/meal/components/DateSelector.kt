package com.messmanager.app.feature.meal.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.res.stringResource
import com.messmanager.app.R
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens
import com.messmanager.app.core.util.formatMedium
import java.time.LocalDate

@Composable
fun DateSelector(
    date: LocalDate,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Rounded.ChevronLeft, contentDescription = stringResource(R.string.content_desc_previous_day))
        }
        Text(
            text = date.formatMedium(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        IconButton(onClick = onNext) {
            Icon(Icons.Rounded.ChevronRight, contentDescription = stringResource(R.string.content_desc_next_day))
        }
    }
}

@ThemePreviews
@Composable
private fun DateSelectorPreview() {
    PreviewSurface {
        DateSelector(
            date = LocalDate.now(),
            onPrevious = {},
            onNext = {},
            modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
        )
    }
}
