package com.numero.range_date_picker_example.range_date_picker

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.numero.range_date_picker_example.R

class WeekView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    init {
        View.inflate(context, R.layout.view_week, this)
    }

    fun getDayView(weekPosition: Int): DayView {
        val parentView = getChildAt(0) as? LinearLayout ?: throw Exception("Not find parent view")
        return parentView.getChildAt(weekPosition) as DayView
    }
}
