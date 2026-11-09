package com.messmanager.app.core.util

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private val mediumDate: DateTimeFormatter =
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

private val monthYear: DateTimeFormatter =
    DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())

fun Long.toLocalDate(): LocalDate = LocalDate.ofEpochDay(this)

fun LocalDate.formatMedium(): String = format(mediumDate)

fun Long.formatEpochDay(): String = toLocalDate().formatMedium()

fun YearMonth.formatMonthYear(): String = format(monthYear)

fun YearMonth.epochRange(): LongRange = atDay(1).toEpochDay()..atEndOfMonth().toEpochDay()
