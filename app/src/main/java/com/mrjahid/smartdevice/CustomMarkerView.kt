package com.mrjahid.smartdevice

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView(
    context: Context,
    layout: Int,
) : MarkerView(context, layout) {

    private var txtViewData: TextView? = null

    init {
        txtViewData = findViewById(R.id.txtViewData)
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        try {
            if (e != null) {
                txtViewData?.text = getDate(e.x.toLong(), "dd/MM/yyyy hh:mm:ss") +"\nValue: "+ e.y.toString()
            }
        } catch (e: IndexOutOfBoundsException) { }

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }

    fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }
}