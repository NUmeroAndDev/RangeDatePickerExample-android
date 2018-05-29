package com.numero.range_date_picker_example.range_date_picker

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.numero.range_date_picker_example.R
import com.numero.range_date_picker_example.range_date_picker.`interface`.OnDayClickListener
import com.numero.range_date_picker_example.range_date_picker.model.CalendarType
import com.numero.range_date_picker_example.range_date_picker.model.Day
import com.numero.range_date_picker_example.range_date_picker.model.Month
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_header.*
import kotlinx.android.synthetic.main.view_holder_month.*

class MonthAdapter(private val monthMap: LinkedHashMap<Month, List<List<Day>>>,
                   private val type: CalendarType,
                   private val onDayClickListener: OnDayClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), StickyHeaderItemDecoration.StickyHeaderInterface {
    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        var headerPosition = -1
        var itemPosition = itemPosition
        while (itemPosition >= 0) {
            if (this.isHeader(itemPosition)) {
                headerPosition = itemPosition
                break
            }
            itemPosition -= 1
        }
        return headerPosition
    }

    override fun getHeaderLayout(headerPosition: Int): Int = R.layout.view_holder_header

    override fun bindHeaderData(header: View, headerPosition: Int) {
        val month = monthMap.keys.toList()[headerPosition / 2]
        if (header is TextView) {
            header.text = "${month.year}/${month.month}"
        }
    }

    override fun isHeader(itemPosition: Int): Boolean {
        return itemPosition % 2 != 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val type = MonthViewType.values()[viewType]
        return when (type) {
            MonthViewType.HEADER -> HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_holder_header, parent, false))
            MonthViewType.CALENDAR -> MonthViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_holder_month, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return monthMap.size * 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 1) {
            MonthViewType.CALENDAR.ordinal
        } else {
            MonthViewType.HEADER.ordinal
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MonthViewHolder) {
            val month = monthMap.keys.toList()[position / 2]
            val monthList = monthMap[month] ?: return
            holder.setUpMonthView(month, monthList, type, onDayClickListener)
        } else if (holder is HeaderViewHolder) {
            val month = monthMap.keys.toList()[position / 2]
            holder.setMonth(month)
        }
    }

    class MonthViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun setUpMonthView(month: Month, dayList: List<List<Day>>, type: CalendarType, listener: OnDayClickListener) {
            monthView.setup(month, dayList, type, listener)
        }

    }

    class HeaderViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun setMonth(month: Month) {
            headerTextView.text = "${month.year}/${month.month}"
        }
    }

    enum class MonthViewType {
        HEADER, CALENDAR
    }
}