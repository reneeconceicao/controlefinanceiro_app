package com.firebaseapp.controlefinanceiro.helpers

import androidx.compose.ui.text.capitalize
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun firstDayOfCurrentMonth(): Date {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

fun lastDayOfCurrentMonth(): Date {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))

    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)

    return calendar.time
}

fun setStartOfDay(calendar: Calendar) {
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
}

fun setEndOfDay(calendar: Calendar) {
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
}

fun toYear(date: Date): String {

    val formatter = SimpleDateFormat(
        "YYYY",
        Locale.getDefault()
    )

    val year = formatter.format(date)
    return year
}

fun toMonthYear(date: Date): String {

    val yearFirst = setOf("ja", "zh", "ko")

    val pattern = if (Locale.getDefault().language in yearFirst) {
        "yyyy/MM"
    } else {
        "MM/yyyy"
    }

    val formatter = SimpleDateFormat(
        pattern,
        Locale.getDefault()
    )

    val monthYear = formatter.format(date)
    return monthYear
}

fun toDateWithWeekDay(date: Date): String {
    val formatterWeek = SimpleDateFormat(
        "EEEE",
        Locale.getDefault()
    )

    val formatter = DateFormat.getDateInstance(
        DateFormat.SHORT,
        Locale.getDefault()
    )

    return "${ formatterWeek.format(date) }, ${formatter.format(date)}"
}

fun toDay(date: Date): String {

    val calendar = Calendar.getInstance()
    calendar.time = date

    val today = Calendar.getInstance()

    if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
        calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
    ) {
        return "Today"
    }

    today.add(Calendar.DAY_OF_YEAR, -1)
    if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
        calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
    ) {
        return "Yesterday"
    }

    today.add(Calendar.DAY_OF_YEAR, 2)
    if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
        calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
    ) {
        return "Tomorrow"
    }

    val formatter = SimpleDateFormat(
        "d MMM",
        Locale.getDefault()
    )

    val result = formatter.format(date).replaceFirstChar {
        it.titlecase(Locale.getDefault())
    }

    return result
}
fun dateToUtcMillis(date: Date): Long {
    val localCal = Calendar.getInstance()
    localCal.time = date

    val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    utcCal.clear()
    utcCal.set(
        localCal.get(Calendar.YEAR),
        localCal.get(Calendar.MONTH),
        localCal.get(Calendar.DAY_OF_MONTH)
    )

    return utcCal.timeInMillis
}

fun utcMillisToDate(millis: Long): Date {
    val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    utcCal.timeInMillis = millis

    val localCal = Calendar.getInstance()
    localCal.clear()
    localCal.set(
        utcCal.get(Calendar.YEAR),
        utcCal.get(Calendar.MONTH),
        utcCal.get(Calendar.DAY_OF_MONTH)
    )

    return localCal.time
}

fun dateToString(date: Date): String {
    val formatter = DateFormat.getDateInstance(DateFormat.SHORT)

//    val weekdayFormatter = SimpleDateFormat(
//        "EEEE",
//        Locale.getDefault()
//    )
    val dateText = formatter.format(date)
//    val weekday = weekdayFormatter.format(date)
    return "$dateText"
}