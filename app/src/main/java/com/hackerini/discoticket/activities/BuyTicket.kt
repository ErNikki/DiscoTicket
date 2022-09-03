package com.hackerini.discoticket.activities

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.views.DayViewContainer
import com.hackerini.discoticket.objects.Club
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

class BuyTicket : AppCompatActivity() {

    val daysOfWeek = arrayOf(
        DayOfWeek.SUNDAY,
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_ticket)

        val club = intent.getSerializableExtra("club") as Club

        val clubName = findViewById<TextView>(R.id.BuyTicketClubName)
        val clubAddress = findViewById<TextView>(R.id.BuyTicketClubAddress)

        clubName.text = club.name
        clubAddress.text = club.address

        val calendarView = findViewById<CalendarView>(R.id.BuyTicketCalendar)

        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                if (day.owner == DayOwner.THIS_MONTH) {
                    container.textView.text = day.date.dayOfMonth.toString()
                    container.textView.setTextColor(Color.BLACK)
                }
                else
                    container.textView.visibility=View.INVISIBLE
                    //container.textView.setTextColor(Color.GRAY)
            }
        }

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)
    }
}