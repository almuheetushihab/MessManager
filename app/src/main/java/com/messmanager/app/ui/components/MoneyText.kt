package com.messmanager.app.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.messmanager.app.R
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens
import com.messmanager.app.core.util.NumberFormatter
import com.messmanager.app.core.util.currentLocale

@Composable
fun formatMoney(amount: Double): String {
    val formatted = NumberFormatter.formatAmount(amount, currentLocale())
    return stringResource(R.string.amount_format, formatted)
}

@Composable
fun MoneyText(
    amount: Double,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    Text(
        text = formatMoney(amount),
        style = style,
        color = color,
        modifier = modifier,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@ThemePreviews
@Composable
private fun MoneyTextPreview() {
    PreviewSurface {
        MoneyText(
            amount = 1250.50,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
        )
    }
}
