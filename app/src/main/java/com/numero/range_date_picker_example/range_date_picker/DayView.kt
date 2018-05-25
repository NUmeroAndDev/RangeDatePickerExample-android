package com.numero.range_date_picker_example.range_date_picker

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.numero.range_date_picker_example.R
import com.numero.range_date_picker_example.range_date_picker.model.Day
import com.numero.range_date_picker_example.range_date_picker.model.RangeState
import kotlinx.android.synthetic.main.view_day.view.*

class DayView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private var listener: ((dayView: DayView) -> Unit)? = null

    var day: Day? = null
        private set

    init {
        View.inflate(context, R.layout.view_day, this)
        dayTextView.setOnClickListener {
            listener?.invoke(this)
        }
    }

    fun setupDay(day: Day) {
        this.day = day
        dayTextView.text = if (day.isSelectable) {
            day.value.toString()
        } else {
            null
        }
//        dayTextView.text = day.value.toString()
        dayTextView.isEnabled = day.isSelectable
        when (day.rangeState) {
            RangeState.FIRST -> setBackgroundResource(R.drawable.range_select_start)
            RangeState.LAST -> setBackgroundResource(R.drawable.range_select_last)
            RangeState.MIDDLE -> setBackgroundResource(R.drawable.range_select_middle)
            else -> {
                if (day.isSelected) {
                    setBackgroundResource(R.drawable.range_select_first)
                } else {
                    background = null
                }
            }
        }
    }

    fun setOnDayClickListener(listener: ((dayView: DayView) -> Unit)) {
        this.listener = listener
    }
}
