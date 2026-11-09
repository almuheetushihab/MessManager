package com.messmanager.app.core.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * Lets ViewModels expose text without holding a Context
 * and without hard coding strings.
 */
sealed interface UiText {
    data class Dynamic(val value: String) : UiText

    class Resource(@StringRes val id: Int, vararg val args: Any) : UiText

    @Composable
    fun asString(): String = when (this) {
        is Dynamic -> value
        is Resource -> stringResource(id, *args)
    }

    fun asString(context: Context): String = when (this) {
        is Dynamic -> value
        is Resource -> context.getString(id, *args)
    }
}
