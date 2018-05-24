package com.numero.range_date_picker_example.range_date_picker

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.numero.range_date_picker_example.R
import com.numero.range_date_picker_example.range_date_picker.model.Day
import com.numero.range_date_picker_example.range_date_picker.model.Month
import com.numero.range_date_picker_example.range_date_picker.model.RangeState
import kotlinx.android.synthetic.main.view_range_date_picker.view.*
import java.util.*

class RangeDatePickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.view_range_date_picker, this)
        val current = Calendar.getInstance()

        val month = Month(2018, 5, current.time, "5")

        val monthAdapter = MonthAdapter(month, getMonthCells(month, current))

        monthRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = monthAdapter
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