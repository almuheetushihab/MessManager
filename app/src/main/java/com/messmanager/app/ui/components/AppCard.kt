package com.messmanager.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
fun AppCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.dimens.cardElevation),
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
            content = content,
        )
    }
}

@ThemePreviews
@Composable
private fun AppCardPreview() {
    PreviewSurface {
        AppCard(modifier = Modifier.padding(MaterialTheme.dimens.spaceM)) {
            Text(stringResource(R.string.sample_card_content))
        }
    }
}
