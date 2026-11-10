package com.messmanager.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.messmanager.app.R
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens
import com.messmanager.app.core.util.formatMonthYear
import java.time.YearMonth

@Composable
fun MonthPicker(
    selectedMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier,
    isClosed: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.dimens.spaceS, vertical = MaterialTheme.dimens.spaceXs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                imageVector = Icons.Rounded.ChevronLeft,
                contentDescription = stringResource(R.string.content_desc_prev_month),
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selectedMonth.formatMonthYear(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            if (isClosed) {
                AssistChip(
                    onClick = {},
                    label = { Text(stringResource(R.string.month_closed_badge)) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = colorResource(R.color.closed_month_container),
                        labelColor = colorResource(R.color.on_closed_month_container),
                    ),
                )
            }
        }
        IconButton(onClick = onNextMonth) {
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = stringResource(R.string.content_desc_next_month),
            )
        }
    }
}

@ThemePreviews
@Composable
private fun MonthPickerOpenPreview() {
    PreviewSurface {
        MonthPicker(
            selectedMonth = YearMonth.of(2026, 11),
            onPreviousMonth = {},
            onNextMonth = {},
            isClosed = false,
        )
    }
}

@ThemePreviews
@Composable
private fun MonthPickerClosedPreview() {
    PreviewSurface {
        MonthPicker(
            selectedMonth = YearMonth.of(2026, 11),
            onPreviousMonth = {},
            onNextMonth = {},
            isClosed = true,
        )
    }
}
