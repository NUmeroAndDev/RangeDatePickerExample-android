package com.numero.range_date_picker_example

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.dialog_range_date_picker.view.*
import java.util.*

class RangeDatePickerDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_range_date_picker, null).apply {
            val minDate = Calendar.getInstance()
            val maxDate = Calendar.getInstance().apply {
                add(Calendar.YEAR, 1)
                add(Calendar.MONTH, 1)
            }
            val start = Calendar.getInstance()
            val end = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, 1)
            }
            rangeDatePickerView.setup(minDate, maxDate, start, end)
        }
        return AlertDialog.Builder(view.context)
                .setView(view)
                .create()
    }

    override fun onStart() {
        super.onStart()

    }

    companion object {
        fun newInstance(): RangeDatePickerDialogFragment = RangeDatePickerDialogFragment()
    }
}