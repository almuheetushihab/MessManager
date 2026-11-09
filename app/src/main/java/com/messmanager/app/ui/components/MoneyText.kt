package com.messmanager.app.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.messmanager.app.R
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens

@Composable
fun formatMoney(amount: Double): String = stringResource(R.string.amount_format, amount)

@ThemePreviews
@Composable
private fun FormatMoneyPreview() {
    PreviewSurface {
        Text(
            text = formatMoney(1250.50),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
        )
    }
}
