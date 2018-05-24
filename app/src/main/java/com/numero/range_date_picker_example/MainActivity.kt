package com.numero.range_date_picker_example

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.numero.range_date_picker_example.range_date_picker.model.Day
import com.numero.range_date_picker_example.range_date_picker.model.Month
import com.numero.range_date_picker_example.range_date_picker.model.RangeState
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val current = Calendar.getInstance()

        val month = Month(2018, 5, current.time, "5")
        monthView.setup(month, getMonthCells(month, current))
    }

    private fun getMonthCells(month: Month, startCal: Calendar): List<List<Day>> {
        val cal = Calendar.getInstance()
        cal.time = startCal.time
        val cells = mutableListOf<List<Day>>()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        var offset = cal.firstDayOfWeek - firstDayOfWeek
        if (offset > 0) {
            offset -= 7
        }
        cal.add(Calendar.DATE, offset)

//        val minSelectedCal = minDate(selectedCals)
//        val maxSelectedCal = maxDate(selectedCals)

        while ((cal.get(Calendar.MONTH) < month.month + 1 || cal.get(Calendar.YEAR) < month.year)
                && cal.get(Calendar.YEAR) <= month.year) {
            val weekCells = mutableListOf<Day>()
            cells.add(weekCells)
            for (c in 0..6) {
                val date = cal.time
                val isCurrentMonth = cal.get(Calendar.MONTH) == month.month
                val isSelected = false
                val isSelectable = true
                val isToday = false
                val isHighlighted = false
                val value = cal.get(Calendar.DAY_OF_MONTH)

                var rangeState = RangeState.NONE
//                if (selectedCals.size > 1) {
//                    if (sameDate(minSelectedCal!!, cal)) {
//                        rangeState = RangeState.FIRST
//                    } else if (sameDate(maxDate(selectedCals)!!, cal)) {
//                        rangeState = RangeState.LAST
//                    } else if (betweenDates(cal, minSelectedCal, maxSelectedCal)) {
//                        rangeState = RangeState.MIDDLE
//                    }
//                }

                weekCells.add(Day(date, isCurrentMonth, isSelectable, isSelected, isToday, isHighlighted, value, rangeState))
                cal.add(Calendar.DATE, 1)
            }
        }
        return cells
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
