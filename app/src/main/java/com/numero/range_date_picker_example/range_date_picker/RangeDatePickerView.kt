package com.numero.range_date_picker_example.range_date_picker

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.numero.range_date_picker_example.R
import com.numero.range_date_picker_example.extension.checkInRange
import com.numero.range_date_picker_example.range_date_picker.model.Day
import com.numero.range_date_picker_example.range_date_picker.model.Month
import com.numero.range_date_picker_example.range_date_picker.model.RangeState
import kotlinx.android.synthetic.main.view_range_date_picker.view.*
import java.util.*
import kotlin.collections.LinkedHashMap

class RangeDatePickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    private val monthMap: LinkedHashMap<Month, List<List<Day>>> = LinkedHashMap()
    private val selectedDays: MutableList<Day> = mutableListOf()
    private val selectedCalendars: MutableList<Calendar> = mutableListOf()

    private var monthAdapter: MonthAdapter

    private var minDate: Calendar = Calendar.getInstance()
    private var maxDate: Calendar = Calendar.getInstance().apply {
        add(Calendar.YEAR, 1)
    }

    private val onDayClickListener: DayClickListener = DayClickListener(minDate, maxDate) { date, day ->
        // 選択処理
        doSelectDate(date, day)
    }

    init {
        View.inflate(context, R.layout.view_range_date_picker, this)

        maxDate = Calendar.getInstance().apply {
            add(Calendar.YEAR, 1)
            add(Calendar.MONTH, 1)
        }
        onDayClickListener.updateRange(minDate, maxDate)
        createMonthList()

        monthAdapter = MonthAdapter(monthMap, onDayClickListener)

        monthRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = monthAdapter
        }
    }

    private fun createMonthList() {
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
            val month = Month(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), date)
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

        val minSelectedCal = if (selectedCalendars.isNotEmpty()) {
            selectedCalendars[0]
        } else {
            null
        }
        val maxSelectedCal = if (selectedCalendars.isNotEmpty()) {
            selectedCalendars[selectedCalendars.size - 1]
        } else {
            null
        }

        while ((cal.get(Calendar.MONTH) < month.month + 1 || cal.get(Calendar.YEAR) < month.year)
                && cal.get(Calendar.YEAR) <= month.year) {
            val weekCells = mutableListOf<Day>()
            cells.add(weekCells)
            for (c in 0..6) {
                val date = cal.time
                val isCurrentMonth = cal.get(Calendar.MONTH) == month.month
                val isSelected = false
                val isSelectable = isCurrentMonth && cal.checkInRange(minDate, maxDate)
                val isToday = false
                val isHighlighted = false
                val value = cal.get(Calendar.DAY_OF_MONTH)

                var rangeState = RangeState.NONE
//                if (selectedCalendars.size > 1) {
//                    if (cal.sameDate(minSelectedCal!!)) {
//                        rangeState = RangeState.FIRST
//                    } else if (cal.sameDate(maxSelectedCal!!)) {
//                        rangeState = RangeState.LAST
//                    } else if (cal.checkInRange(minSelectedCal, maxSelectedCal)) {
//                        rangeState = RangeState.MIDDLE
//                    }
//                }

                weekCells.add(Day(date, isCurrentMonth, isSelectable, isSelected, isToday, isHighlighted, value, rangeState))
                cal.add(Calendar.DATE, 1)
            }
        }
        return cells
    }

    private fun doSelectDate(date: Calendar, day: Day) {
        val newlySelectedCal = Calendar.getInstance()
        newlySelectedCal.time = date.time
        // Sanitize input: clear out the hours/minutes/seconds/millis.
//        setMidnight(newlySelectedCal)

        // Clear any remaining range state.
        for (selectedCell in selectedDays) {
            selectedCell.rangeState = RangeState.NONE
        }

        if (selectedCalendars.size > 1) {
            // We've already got a range selected: clear the old one.
            clearOldSelections()
        } else if (selectedCalendars.size == 1 && newlySelectedCal.before(selectedCalendars[0])) {
            // We're moving the start of the range back in time: clear the old start date.
            clearOldSelections()
        }

        // Select a new cell.
        if (selectedDays.size == 0 || selectedDays[0] != day) {
            selectedDays.add(day)
            day.isSelected = true
        }
        selectedCalendars.add(newlySelectedCal)

        if (selectedDays.size > 1) {
            // Select all days in between start and end.
            val start = selectedDays[0].date
            val end = selectedDays[1].date
            selectedDays[0].rangeState = RangeState.FIRST
            selectedDays[1].rangeState = RangeState.LAST

            val startMonthIndex = monthMap.keys.indexOfFirst {
                val c = Calendar.getInstance().apply {
                    time = selectedCalendars[0].time
                }
                it.year == c.get(Calendar.YEAR) && it.month == c.get(Calendar.MONTH)
            }
            val endMonthIndex = monthMap.keys.indexOfFirst {
                val c = Calendar.getInstance().apply {
                    time = selectedCalendars[1].time
                }
                it.year == c.get(Calendar.YEAR) && it.month == c.get(Calendar.MONTH)
            }
            for (monthIndex in startMonthIndex..endMonthIndex) {
                val month = monthMap.keys.toList()[monthIndex]
                val days = monthMap[month] ?: listOf()
                for (week in days) {
                    for (singleCell in week) {
                        if (singleCell.date.after(start) and singleCell.date.before(end) and singleCell.isSelectable) {
                            singleCell.isSelected = true
                            singleCell.rangeState = RangeState.MIDDLE
                            selectedDays.add(singleCell)
                        }
                    }
                }
            }
        }

        monthAdapter.notifyDataSetChanged()
    }

    private fun clearOldSelections() {
        selectedDays.forEach {
            // 選択解除
            it.isSelected = false
        }
        selectedDays.clear()
        selectedCalendars.clear()
    }
}