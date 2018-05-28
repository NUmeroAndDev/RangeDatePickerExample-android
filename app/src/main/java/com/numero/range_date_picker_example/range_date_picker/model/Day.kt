package com.numero.range_date_picker_example.range_date_picker.model

import java.util.*

data class Day(
        val date: Date,
        val day: Int,
        val isSelectable: Boolean,
        var isSelected: Boolean,
        val isToday: Boolean,
        var rangeState: RangeState)
