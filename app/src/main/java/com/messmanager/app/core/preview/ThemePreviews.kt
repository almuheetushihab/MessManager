package com.messmanager.app.core.preview

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.messmanager.app.core.theme.MessTheme

@Preview(name = "Light", showBackground = true, showSystemUi = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, showSystemUi = true)
@Preview(name = "Bangla", locale = "bn", showBackground = true, showSystemUi = true)
annotation class ThemePreviews

@Composable
fun PreviewSurface(
    content: @Composable () -> Unit,
) {
    MessTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            content = content,
        )
    }
}
