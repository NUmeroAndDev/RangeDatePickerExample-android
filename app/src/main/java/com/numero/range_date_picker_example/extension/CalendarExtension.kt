package com.numero.range_date_picker_example.extension

import java.text.SimpleDateFormat
import java.util.*

fun Calendar.checkInRange(minDate: Calendar?, maxDate: Calendar?): Boolean {
    minDate ?: return false
    maxDate ?: return false
    return (before(minDate) || after(maxDate)).not()
}

fun Calendar.sameDate(calendar: Calendar): Boolean {
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    return get(Calendar.YEAR) == year && get(Calendar.MONTH) == month && get(Calendar.DAY_OF_MONTH) == day
}

fun Calendar.cutTime(): Calendar {
    val year = get(Calendar.YEAR)
    val month = get(Calendar.MONTH)
    val day = get(Calendar.DAY_OF_MONTH)
    clear()
    set(year, month, day)
    return this
}

fun Calendar.format(format: String): String {
    return SimpleDateFormat(format, Locale.US).format(time)
}