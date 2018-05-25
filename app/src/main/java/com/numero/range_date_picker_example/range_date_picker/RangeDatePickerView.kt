package com.numero.range_date_picker_example.range_date_picker

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.numero.range_date_picker_example.R
import com.numero.range_date_picker_example.extension.format
import com.numero.range_date_picker_example.range_date_picker.model.Day
import com.numero.range_date_picker_example.range_date_picker.model.Month
import com.numero.range_date_picker_example.range_date_picker.model.RangeState
import kotlinx.android.synthetic.main.view_range_date_picker.view.*
import java.util.*
import kotlin.collections.LinkedHashMap

class RangeDatePickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    private val monthMap: LinkedHashMap<Month, List<List<Day>>> = LinkedHashMap()

    init {
        View.inflate(context, R.layout.view_range_date_picker, this)
        val current = Calendar.getInstance()

        val calendar = Calendar.getInstance().apply {
            add(Calendar.YEAR, 1)
            add(Calendar.MONTH, 1)
        }
        createMonthList(maxDate = calendar)

        val monthAdapter = MonthAdapter(monthMap)

        monthRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = monthAdapter
        }
    }

    private fun createMonthList(minDate: Calendar = Calendar.getInstance(), maxDate: Calendar) {
        val calendar = Calendar.getInstance().apply {
            val minYear = minDate.get(Calendar.YEAR)
            val minMonth = minDate.get(Calendar.MONTH)
            set(minYear, minMonth, 1)
        }

        val maxYear = maxDate.get(Calendar.YEAR)
        val maxMonth = maxDate.get(Calendar.MONTH)
        while ((calendar.get(Calendar.MONTH) <= maxMonth // Up to, including the month.
                        || calendar.get(Calendar.YEAR) < maxYear) // Up to the year.
                && calendar.get(Calendar.YEAR) < maxYear + 1) { // But not > next yr.
            val date = calendar.time
            val month = Month(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    date, calendar.format("YYYY/MM"))
            monthMap[month] = getMonthCells(month, calendar)
            calendar.add(Calendar.MONTH, 1)
        }
    }

    private fun getMonthCells(month: Month, startCal: Calendar): List<List<Day>> {
        val cal = Calendar.getInstance()
        cal.time = startCal.time
        val cells = mutableListOf<List<Day>>()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        var offset = cal.firstDayOfWeek - firstDayOfWeek
        if (offset > 0) {
            offset -= 7
        }
        cal.add(Calendar.DATE, offset)

//        val minSelectedCal = minDate(selectedCals)
//        val maxSelectedCal = maxDate(selectedCals)

        while ((cal.get(Calendar.MONTH) < month.month + 1 || cal.get(Calendar.YEAR) < month.year)
                && cal.get(Calendar.YEAR) <= month.year) {
            val weekCells = mutableListOf<Day>()
            cells.add(weekCells)
            for (c in 0..6) {
                val date = cal.time
                val isCurrentMonth = cal.get(Calendar.MONTH) == month.month
                val isSelected = false
                val isSelectable = true
                val isToday = false
                val isHighlighted = false
                val value = cal.get(Calendar.DAY_OF_MONTH)

                var rangeState = RangeState.NONE
//                if (selectedCals.size > 1) {
//                    if (sameDate(minSelectedCal!!, cal)) {
//                        rangeState = RangeState.FIRST
//                    } else if (sameDate(maxDate(selectedCals)!!, cal)) {
//                        rangeState = RangeState.LAST
//                    } else if (betweenDates(cal, minSelectedCal, maxSelectedCal)) {
//                        rangeState = RangeState.MIDDLE
//                    }
//                }

                weekCells.add(Day(date, isCurrentMonth, isSelectable, isSelected, isToday, isHighlighted, value, rangeState))
                cal.add(Calendar.DATE, 1)
            }
        }
        return cells
    }
}