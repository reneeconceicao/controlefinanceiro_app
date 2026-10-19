package com.firebaseapp.controlefinanceiro.helpers


import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import com.firebaseapp.controlefinanceiro.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

fun firstDayOfCurrentMonth(): LocalDateTime =
    LocalDate.now()
        .withDayOfMonth(1)
        .atStartOfDay()

fun lastDayOfCurrentMonth(): LocalDateTime =
    YearMonth.now()
        .atEndOfMonth()
        .atTime(23, 59, 59, 999_000_000)

fun setStartOfDay(date: LocalDateTime): LocalDateTime =
    date.toLocalDate().atStartOfDay()

fun setEndOfDay(date: LocalDateTime): LocalDateTime =
    date.toLocalDate().atTime(23, 59, 59, 999_000_000)

fun toYear(date: LocalDateTime): String =
    date.format(DateTimeFormatter.ofPattern("yyyy", Locale.getDefault()))

fun toMonthYear(date: LocalDateTime): String {
    val locale = Locale.getDefault()
    val pattern = if (locale.language in setOf("ja", "zh", "ko")) {
        "yyyy/MM"
    } else {
        "MM/yyyy"
    }

    return date.format(DateTimeFormatter.ofPattern(pattern, locale))
}

fun toDateWithWeekDay(date: LocalDateTime): String {
    val locale = Locale.getDefault()

    val weekday = date.format(
        DateTimeFormatter.ofPattern("EEEE", locale)
    )

    val formattedDate = date.format(
        DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
            .withLocale(locale)
    )

    return "$weekday, $formattedDate"
}

@Composable
fun toDay(date: LocalDateTime): String {
    val today = LocalDate.now()
    val dateOnly = date.toLocalDate()

    if (dateOnly == today) {
        return stringResource(R.string.today)
    }

    if (dateOnly == today.minusDays(1)) {
        return stringResource(R.string.yesterday)
    }

    if (dateOnly == today.plusDays(1)) {
        return stringResource(R.string.tomorrow)
    }

    val locale = LocalLocale.current.platformLocale

    return date.format(
        DateTimeFormatter.ofPattern("d MMM", locale)
    ).replaceFirstChar { it.titlecase(locale) }
}

fun dateToUtcMillis(date: LocalDateTime): Long =
    date.toLocalDate()
        .atStartOfDay(java.time.ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()

fun utcMillisToDate(millis: Long): LocalDateTime =
    java.time.Instant.ofEpochMilli(millis)
        .atZone(java.time.ZoneOffset.UTC)
        .toLocalDateTime()
        .toLocalDate()
        .atStartOfDay()

@Composable
fun dateToString(date: LocalDateTime): String {
    val dateOnly = date.toLocalDate()
    val today = LocalDate.now()

    if (dateOnly == today) {
        return stringResource(R.string.today)
    }

    if (dateOnly == today.minusDays(1)) {
        return stringResource(R.string.yesterday)
    }

    if (dateOnly == today.plusDays(1)) {
        return stringResource(R.string.tomorrow)
    }

    return date.format(
        DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
            .withLocale(LocalLocale.current.platformLocale)
    )
}

fun adjustMonth(
    date: LocalDateTime,
    month: LocalDateTime
): LocalDateTime {
    val targetMonth = YearMonth.from(month)
    val day = minOf(date.dayOfMonth, targetMonth.lengthOfMonth())

    return date.withYear(targetMonth.year)
        .withMonth(targetMonth.monthValue)
        .withDayOfMonth(day)
}
