package com.numero.range_date_picker_example.range_date_picker

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.numero.range_date_picker_example.R
import com.numero.range_date_picker_example.range_date_picker.`interface`.OnDayClickListener
import com.numero.range_date_picker_example.range_date_picker.model.CalendarType
import com.numero.range_date_picker_example.range_date_picker.model.Day

class MonthView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.view_month, this)
    }

    fun setup(dayList: List<List<Day>>, type: CalendarType, listener: OnDayClickListener) {
        val numRows = dayList.size
        for (i in 0 until WEEK_COUNT) {
            val weekView = getWeekView(i)
            weekView.setOnDayClickListener(listener)
            if (i < numRows) {
                weekView.visibility = View.VISIBLE
                dayList[i].forEachIndexed { index, day ->
                    val cellView = weekView.getDayView(index)
                    cellView.setupDay(day)
                }
            } else {
                weekView.visibility = when (type) {
                    CalendarType.VERTICAL_SCROLL -> View.GONE
                    CalendarType.PAGER -> View.INVISIBLE
                }
            }
        }
    }

    private fun getWeekView(weekPosition: Int): WeekView {
        val parentView = getChildAt(0) as? LinearLayout ?: throw Exception("Not find parent view")
        return parentView.getChildAt(weekPosition) as WeekView
    }

    companion object {
        private const val WEEK_COUNT: Int = 6
    }

}