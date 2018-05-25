package com.numero.range_date_picker_example.range_date_picker

import com.numero.range_date_picker_example.extension.checkInRange
import com.numero.range_date_picker_example.range_date_picker.`interface`.OnDayClickListener
import com.numero.range_date_picker_example.range_date_picker.model.Day
import java.util.*

class DayClickListener(private var minDate: Calendar, private var maxDate: Calendar, private val listener: (() -> Unit)) : OnDayClickListener {

    fun updateRange(minDate: Calendar, maxDate: Calendar) {
        this.minDate = minDate
        this.maxDate = maxDate
    }

    override fun onClickDay(day: Day) {
        val clickedDate = Calendar.getInstance().apply {
            time = day.date
        }
        if (clickedDate.checkInRange(minDate, maxDate)) {
            listener.invoke()
        }
    }

}