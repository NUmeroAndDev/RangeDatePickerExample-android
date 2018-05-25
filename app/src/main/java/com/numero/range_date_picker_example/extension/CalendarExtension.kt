package com.numero.range_date_picker_example.extension

import java.text.SimpleDateFormat
import java.util.*

fun Calendar.checkInRange(minDate: Calendar?, maxDate: Calendar?): Boolean {
    minDate ?: return false
    maxDate ?: return false
    return (before(minDate) || after(maxDate)).not()
}

fun Calendar.format(format: String): String {
    return SimpleDateFormat(format).format(time)
}