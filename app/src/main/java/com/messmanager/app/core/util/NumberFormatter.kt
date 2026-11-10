package com.messmanager.app.core.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import java.text.DecimalFormat
import java.util.Locale

object NumberFormatter {
    private val decimalFormat = DecimalFormat("#,##0.00")
    private val intFormat = DecimalFormat("#,##0")

    fun formatAmount(amount: Double, locale: Locale = Locale.getDefault()): String {
        val formatted = decimalFormat.format(amount)
        return if (locale.language == "bn") {
            formatted.toBanglaDigits()
        } else {
            formatted
        }
    }

    fun formatInt(number: Int, locale: Locale = Locale.getDefault()): String {
        val formatted = intFormat.format(number)
        return if (locale.language == "bn") {
            formatted.toBanglaDigits()
        } else {
            formatted
        }
    }

    fun String.toBanglaDigits(): String {
        return this.map { char ->
            when (char) {
                '0' -> '০'
                '1' -> '১'
                '2' -> '২'
                '3' -> '৩'
                '4' -> '৪'
                '5' -> '৫'
                '6' -> '৬'
                '7' -> '৭'
                '8' -> '৮'
                '9' -> '৯'
                else -> char
            }
        }.joinToString("")
    }
}

@Composable
fun currentLocale(): Locale {
    val configuration = LocalConfiguration.current
    return configuration.locales[0]
}

@Composable
fun formatNumber(number: Int): String {
    return NumberFormatter.formatInt(number, currentLocale())
}
