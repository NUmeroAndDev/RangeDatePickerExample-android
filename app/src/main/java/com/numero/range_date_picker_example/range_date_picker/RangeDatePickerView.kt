package com.numero.range_date_picker_example.range_date_picker

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.numero.range_date_picker_example.R
import com.numero.range_date_picker_example.extension.checkInRange
import com.numero.range_date_picker_example.extension.cutTime
import com.numero.range_date_picker_example.extension.isFirstDay
import com.numero.range_date_picker_example.extension.isLastDay
import com.numero.range_date_picker_example.range_date_picker.model.Day
import com.numero.range_date_picker_example.range_date_picker.model.Month
import com.numero.range_date_picker_example.range_date_picker.model.RangeState
import kotlinx.android.synthetic.main.view_range_date_picker.view.*
import java.util.*
import kotlin.collections.LinkedHashMap

class RangeDatePickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    private val monthMap: LinkedHashMap<Month, List<List<Day>>> = LinkedHashMap()
    private val selectedDayList: MutableList<Day> = mutableListOf()
    private val selectedCalendarList: MutableList<Calendar> = mutableListOf()

    private var monthAdapter: MonthAdapter

    private var minDate: Calendar = Calendar.getInstance().cutTime()
    private var maxDate: Calendar = Calendar.getInstance().cutTime().apply {
        add(Calendar.YEAR, 1)
    }

    private val onDayClickListener: DayClickListener = DayClickListener(minDate, maxDate) {
        // 選択処理
        doSelectDate(it)
    }

    init {
        View.inflate(context, R.layout.view_range_date_picker, this)

        maxDate = Calendar.getInstance().cutTime().apply {
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
        val calendar = Calendar.getInstance().cutTime().apply {
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
        val baseCalendar = Calendar.getInstance().apply {
            time = startCal.time
            cutTime()
        }
        val cells = mutableListOf<List<Day>>()
        baseCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = baseCalendar.get(Calendar.DAY_OF_WEEK)
        var offset = baseCalendar.firstDayOfWeek - firstDayOfWeek
        if (offset > 0) {
            offset -= 7
        }
        baseCalendar.add(Calendar.DATE, offset)

        val minSelectedCal = if (selectedCalendarList.isNotEmpty()) {
            selectedCalendarList[0]
        } else {
            null
        }
        val maxSelectedCal = if (selectedCalendarList.isNotEmpty()) {
            selectedCalendarList[selectedCalendarList.size - 1]
        } else {
            null
        }

        while ((baseCalendar.get(Calendar.MONTH) < month.month + 1 || baseCalendar.get(Calendar.YEAR) < month.year)
                && baseCalendar.get(Calendar.YEAR) <= month.year) {
            val weekCells = mutableListOf<Day>()
            cells.add(weekCells)
            for (c in 0..6) {
                val date = baseCalendar.time
                val isCurrentMonth = baseCalendar.get(Calendar.MONTH) == month.month
                val isSelected = false
                val isSelectable = isCurrentMonth && baseCalendar.checkInRange(minDate, maxDate)
                val isToday = false
                val isHighlighted = false
                val value = baseCalendar.get(Calendar.DAY_OF_MONTH)

                var rangeState = RangeState.NONE
//                if (selectedCalendarList.size > 1) {
//                    if (cal.sameDate(minSelectedCal!!)) {
//                        rangeState = RangeState.FIRST
//                    } else if (cal.sameDate(maxSelectedCal!!)) {
//                        rangeState = RangeState.LAST
//                    } else if (cal.checkInRange(minSelectedCal, maxSelectedCal)) {
//                        rangeState = RangeState.MIDDLE
//                    }
//                }

                weekCells.add(Day(date, isCurrentMonth, isSelectable, isSelected, isToday, isHighlighted, value, rangeState))
                baseCalendar.add(Calendar.DATE, 1)
            }
        }
        return cells
    }

    private fun doSelectDate(selectedDay: Day) {
        val newlySelectedCal = Calendar.getInstance().apply {
            time = selectedDay.date
        }

        selectedDayList.forEach {
            // 初期化処理
            it.rangeState = RangeState.NONE
        }

        if (selectedCalendarList.size > 1) {
            clearOldSelections()
        } else if (selectedCalendarList.size == 1 && newlySelectedCal.before(selectedCalendarList[0])) {
            clearOldSelections()
        }

        if (selectedDayList.size == 0 || selectedDayList[0] != selectedDay) {
            selectedDayList.add(selectedDay)
            selectedDay.isSelected = true
        }
        selectedCalendarList.add(newlySelectedCal)

        if (selectedDayList.size > 1) {
            // Select all days in between start and end.
            val start = Calendar.getInstance().apply {
                time = selectedDayList[0].date
                cutTime()
            }
            val end = Calendar.getInstance().apply {
                time = selectedDayList[1].date
                cutTime()
            }
            // 最初が月の最後の場合、丸アイコンにする
            selectedDayList[0].rangeState = when {
                start.isLastDay() -> RangeState.FIRST_AND_LAST
                else -> RangeState.FIRST
            }
            // 最後が月の最初の場合、丸アイコンにする
            selectedDayList[1].rangeState = when {
                end.isFirstDay() -> RangeState.FIRST_AND_LAST
                else -> RangeState.LAST
            }

            val startMonthIndex = monthMap.keys.indexOfFirst {
                val c = Calendar.getInstance().apply {
                    time = selectedCalendarList[0].time
                }
                it.year == c.get(Calendar.YEAR) && it.month == c.get(Calendar.MONTH)
            }
            val endMonthIndex = monthMap.keys.indexOfFirst {
                val c = Calendar.getInstance().apply {
                    time = selectedCalendarList[1].time
                }
                it.year == c.get(Calendar.YEAR) && it.month == c.get(Calendar.MONTH)
            }
            for (monthIndex in startMonthIndex..endMonthIndex) {
                val month = monthMap.keys.toList()[monthIndex]
                val dayList = monthMap[month] ?: listOf()
                for (week in dayList) {
                    for (day in week) {
                        val dayCalendar = Calendar.getInstance().apply {
                            time = day.date
                        }
                        if (dayCalendar.after(start) and dayCalendar.before(end) and day.isSelectable) {
                            day.isSelected = true
                            day.rangeState = when {
                                dayCalendar.isLastDay() -> RangeState.LAST
                                dayCalendar.isFirstDay() -> RangeState.FIRST
                                else -> RangeState.MIDDLE
                            }
                            selectedDayList.add(day)
                        }
                    }
                }
            }
        }

        monthAdapter.notifyDataSetChanged()
    }

    private fun clearOldSelections() {
        selectedDayList.forEach {
            // 選択解除
            it.isSelected = false
        }
        selectedDayList.clear()
        selectedCalendarList.clear()
    }
}