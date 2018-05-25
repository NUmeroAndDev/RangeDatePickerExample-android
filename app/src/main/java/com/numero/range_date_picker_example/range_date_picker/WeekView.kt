package com.numero.range_date_picker_example.range_date_picker

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.numero.range_date_picker_example.range_date_picker.`interface`.OnDayClickListener

class WeekView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    private var listener: OnDayClickListener? = null

    init {
        for (i in 0 until WEEK_HAS_DAY_COUNT) {
            val dayView = DayView(context)
            dayView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1F)
            dayView.setOnDayClickListener {
                val day = it.day ?: return@setOnDayClickListener
                listener?.onClickDay(day)
            }
            addView(dayView)
        }
    }

    fun setOnDayClickListener(listener: OnDayClickListener?) {
        this.listener = listener
    }

    fun getDayView(weekPosition: Int): DayView {
        return getChildAt(weekPosition) as DayView
    }

    companion object {
        private const val WEEK_HAS_DAY_COUNT = 7
    }
}
