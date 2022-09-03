package com.hackerini.discoticket.fragments.views

import android.view.View
import android.widget.TextView
import com.hackerini.discoticket.R
import com.kizitonwose.calendarview.ui.ViewContainer

class MonthViewContainer(view: View) : ViewContainer(view) {
    val textView = view.findViewById<TextView>(R.id.headerTextView)
}
