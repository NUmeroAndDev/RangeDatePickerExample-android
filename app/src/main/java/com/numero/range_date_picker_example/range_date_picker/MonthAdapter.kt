package com.numero.range_date_picker_example.range_date_picker

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.numero.range_date_picker_example.R
import com.numero.range_date_picker_example.range_date_picker.model.Day
import com.numero.range_date_picker_example.range_date_picker.model.Month
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_holder_month.*

class MonthAdapter(private val month: Month, private val dayList: List<List<Day>>) : RecyclerView.Adapter<MonthAdapter.MonthViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        return MonthViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_holder_month, parent, false))
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        holder.setUpMonthView(month, dayList)
    }


    class MonthViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun setUpMonthView(month: Month, dayList: List<List<Day>>) {
            monthView.setup(month, dayList)
        }

    }
}