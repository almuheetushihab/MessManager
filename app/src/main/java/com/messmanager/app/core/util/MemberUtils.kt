package com.messmanager.app.core.util

import java.util.Locale

/**
 * Capitalizes the first letter of each word in a string.
 */
fun String.capitalizeWords(): String {
    if (isBlank()) return this
    return trim().split(Regex("\\s+")).joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}
