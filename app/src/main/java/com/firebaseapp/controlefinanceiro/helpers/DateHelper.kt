package com.firebaseapp.controlefinanceiro.helpers

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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