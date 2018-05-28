package com.numero.range_date_picker_example.range_date_picker

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.numero.range_date_picker_example.R
import com.numero.range_date_picker_example.extension.*
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

        monthAdapter = MonthAdapter(monthMap, onDayClickListener)

        monthRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = monthAdapter
        }
    }

    /**
     * @param minDate 開始日
     * @param maxDate 終了日
     * @param firstSelectedDate 初期選択
     * @param lastSelectedDate 初期選択
     */
    fun setup(minDate: Calendar, maxDate: Calendar, firstSelectedDate: Calendar? = null, lastSelectedDate: Calendar? = null) {
        this.minDate = minDate
        this.maxDate = maxDate

        //初期選択
        if (firstSelectedDate != null && lastSelectedDate != null) {
            selectedCalendarList.add(firstSelectedDate.cutTime())
            selectedCalendarList.add(lastSelectedDate.cutTime())
        }

        onDayClickListener.updateRange(minDate, maxDate)
        createMonthList()
    }

    private fun createMonthList() {
        val calendar = Calendar.getInstance().cutTime().apply {
            val minYear = minDate.get(Calendar.YEAR)
            val minMonth = minDate.get(Calendar.MONTH)
            set(minYear, minMonth, 1)
        }

        val maxYear = maxDate.get(Calendar.YEAR)
        val maxMonth = maxDate.get(Calendar.MONTH)
        while ((calendar.get(Calendar.MONTH) <= maxMonth || calendar.get(Calendar.YEAR) < maxYear) && calendar.get(Calendar.YEAR) < maxYear + 1) {
            val date = calendar.time
            val month = Month(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), date)
            monthMap[month] = getMonthCells(month, calendar)
            calendar.add(Calendar.MONTH, 1)
        }
    }

    private fun getMonthCells(month: Month, startCal: Calendar): List<List<Day>> {
        val baseCalendar = Calendar.getInstance().apply {
            time = startCal.time
            set(Calendar.DAY_OF_MONTH, 1)
        }

        val firstDayOfWeek = baseCalendar.get(Calendar.DAY_OF_WEEK)
        var offset = baseCalendar.firstDayOfWeek - firstDayOfWeek
        if (offset > 0) {
            offset -= 7
        }
        baseCalendar.add(Calendar.DATE, offset)

        val minSelectedCal = if (selectedCalendarList.isNotEmpty()) {
            selectedCalendarList.first()
        } else {
            null
        }
        val maxSelectedCal = if (selectedCalendarList.isNotEmpty()) {
            selectedCalendarList.last()
        } else {
            null
        }

        val weekList = mutableListOf<List<Day>>()
        while ((baseCalendar.get(Calendar.MONTH) < month.month + 1 || baseCalendar.get(Calendar.YEAR) < month.year) && baseCalendar.get(Calendar.YEAR) <= month.year) {
            val dayList = mutableListOf<Day>()
            for (c in 0 until WeekView.WEEK_HAS_DAY_COUNT) {
                val date = baseCalendar.time
                val value = baseCalendar.get(Calendar.DAY_OF_MONTH)
                val isCurrentMonth = baseCalendar.get(Calendar.MONTH) == month.month
                val isSelected = false
                val isSelectable = isCurrentMonth && baseCalendar.checkInRange(minDate, maxDate)
                val isToday = false

                val day = Day(date, value, isCurrentMonth, isSelectable, isSelected, isToday, RangeState.NONE)
                // 初期選択処理
                if (minSelectedCal != null && maxSelectedCal != null && isSelectable && baseCalendar.checkInRange(minSelectedCal, maxSelectedCal)) {
                    day.rangeState = when {
                        baseCalendar.sameDate(minSelectedCal) -> {
                            if (minSelectedCal.isLastDay()) {
                                RangeState.FIRST_AND_LAST
                            } else {
                                RangeState.FIRST
                            }
                        }
                        baseCalendar.sameDate(maxSelectedCal) -> {
                            if (maxSelectedCal.isFirstDay()) {
                                RangeState.FIRST_AND_LAST
                            } else {
                                RangeState.LAST
                            }
                        }
                        baseCalendar.isLastDay() -> RangeState.LAST
                        baseCalendar.isFirstDay() -> RangeState.FIRST
                        else -> RangeState.MIDDLE
                    }
                    selectedDayList.add(day)
                }

                dayList.add(day)
                baseCalendar.add(Calendar.DATE, 1)
            }
            weekList.add(dayList)
        }
        return weekList
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
        } else if (selectedCalendarList.size == 1 && newlySelectedCal.before(selectedCalendarList.first())) {
            clearOldSelections()
        }

        if (selectedDayList.size == 0 || selectedDayList.first() != selectedDay) {
            selectedDayList.add(selectedDay)
            selectedDay.isSelected = true
        }
        selectedCalendarList.add(newlySelectedCal)

        if (selectedDayList.size > 1) {
            val start = Calendar.getInstance().apply {
                time = selectedDayList.first().date
                cutTime()
            }
            val end = Calendar.getInstance().apply {
                time = selectedDayList.last().date
                cutTime()
            }
            // 最初が月の最後の場合、丸アイコンにする
            selectedDayList.first().rangeState = when {
                start.isLastDay() -> RangeState.FIRST_AND_LAST
                else -> RangeState.FIRST
            }
            // 最後が月の最初の場合、丸アイコンにする
            selectedDayList.last().rangeState = when {
                end.isFirstDay() -> RangeState.FIRST_AND_LAST
                else -> RangeState.LAST
            }

            val startMonthIndex = monthMap.keys.indexOfFirst {
                val c = Calendar.getInstance().apply {
                    time = selectedCalendarList.first().time
                }
                it.year == c.get(Calendar.YEAR) && it.month == c.get(Calendar.MONTH)
            }
            val endMonthIndex = monthMap.keys.indexOfFirst {
                val c = Calendar.getInstance().apply {
                    time = selectedCalendarList.last().time
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