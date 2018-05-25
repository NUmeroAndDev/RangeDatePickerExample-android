package com.numero.range_date_picker_example.range_date_picker

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.numero.range_date_picker_example.R
import com.numero.range_date_picker_example.range_date_picker.model.Day
import kotlinx.android.synthetic.main.view_day.view.*

class DayView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private var listener: ((dayView: DayView) -> Unit)? = null

    var day: Day? = null
        set(value) {
            value ?: return
            field = value
            dayTextView.text = value.value.toString()
            dayTextView.isEnabled = value.isSelectable
        }

    init {
        View.inflate(context, R.layout.view_day, this)
        dayTextView.setOnClickListener {
            listener?.invoke(this)
        }
    }

    fun setOnDayClickListener(listener: ((dayView: DayView) -> Unit)) {
        this.listener = listener
    }
}
