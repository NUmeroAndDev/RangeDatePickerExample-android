package com.numero.range_date_picker_example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.numero.range_date_picker_example.extension.cutTime
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        val minDate = Calendar.getInstance().cutTime()
        val maxDate = Calendar.getInstance().cutTime().apply {
            add(Calendar.YEAR, 1)
            add(Calendar.MONTH, 1)
        }
        monthView.setup(minDate, maxDate, Calendar.getInstance(), Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                RangeDatePickerDialogFragment.newInstance().show(supportFragmentManager, "")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
