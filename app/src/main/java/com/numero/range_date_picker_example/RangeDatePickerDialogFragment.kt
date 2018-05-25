package com.numero.range_date_picker_example

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater

class RangeDatePickerDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_range_date_picker, null)
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