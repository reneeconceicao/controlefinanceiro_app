package com.firebaseapp.controlefinanceiro.helpers

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

fun toMonthYear(date: Date): String {

    val formatter = SimpleDateFormat(
        "MMMM YYYY",
        Locale.getDefault()
    )

    val monthYear = formatter.format(date)
    return monthYear
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