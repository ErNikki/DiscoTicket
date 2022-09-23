package com.hackerini.discoticket.activities

import android.app.ActionBar
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.EventElement
import com.hackerini.discoticket.fragments.views.DayViewContainer
import com.hackerini.discoticket.fragments.views.MonthViewContainer
import com.hackerini.discoticket.fragments.views.QuanitySelector
import com.hackerini.discoticket.objects.*
import com.hackerini.discoticket.utils.ObjectLoader
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*

class BuyTicket : AppCompatActivity() {

    private var selectedDate: CalendarDay? = null
    private var amountOfSimpleTicket = 0
    private var amountOfTableTicket = 0
    private var club: Club? = null
    private var event: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_ticket)

        club = intent.getSerializableExtra("club") as Club
        event = intent.getSerializableExtra("event") as Event?
        val events = ObjectLoader.getEvents(this).filter { event -> event.club?.id == club?.id }

        val clubName = findViewById<TextView>(R.id.BuyTicketClubName)
        val clubAddress = findViewById<TextView>(R.id.BuyTicketClubAddress)
        val simpleTicketPrice = findViewById<TextView>(R.id.BuyTicketSimpleTicketPrice)
        val tableTicketPrice = findViewById<TextView>(R.id.BuyTicketTableTicketPrice)
        val payButton = findViewById<Button>(R.id.BuyTicketPayButton)
        val viewTable = findViewById<Button>(R.id.BuyTicketViewTableButton)
        val simpleTicketCounter =
            findViewById<FrameLayout>(R.id.BuyTicketAmountSimpleTicket)
        val tableTicketCounter =
            findViewById<FrameLayout>(R.id.BuyTicketAmountTableTicket)

        val simpleQuantitySelector = QuanitySelector.newInstance()
        val tableQuantitySelector = QuanitySelector.newInstance()
        simpleQuantitySelector.onChangeListener = { s ->
            amountOfSimpleTicket = s
            updateCartTotal()
        }
        tableQuantitySelector.onChangeListener = { s ->
            amountOfTableTicket = s
            updateCartTotal()
        }

        supportFragmentManager
            .beginTransaction()
            .add(simpleTicketCounter.id, simpleQuantitySelector)
            .add(tableTicketCounter.id, tableQuantitySelector)
            .commit()

        clubName.text = club?.name
        clubAddress.text = club?.address

        val simpleSpannableString = SpannableString(String.format("%.2f", club?.simpleTicketPrice) + "€")
        val tableSpannableString = SpannableString(String.format("%.2f", club?.tableTicketPrice) + "€")
        simpleSpannableString.setSpan(StyleSpan(Typeface.BOLD), 0, simpleSpannableString.length, 0)
        tableSpannableString.setSpan(StyleSpan(Typeface.BOLD), 0, simpleSpannableString.length, 0)
        var simplePrice = TextUtils.concat(simpleSpannableString, "/persona")
        var tablePrice = TextUtils.concat(tableSpannableString, "/persona")
        simpleTicketPrice.text = simplePrice
        tableTicketPrice.text = tablePrice

        val calendarView = findViewById<CalendarView>(R.id.BuyTicketCalendar)
        calendarView.dayBinder = object : DayBinder<DayViewContainer> {

            // Called only when a new container is needed.
            override fun create(view: View): DayViewContainer {
                return DayViewContainer(view, calendarView)
            }

            // Called every time we need to reuse a container.
            var lastHighlighted: TextView? = null

            override fun bind(container: DayViewContainer, day: CalendarDay) {
                val textView = container.textView
                container.day = day
                textView.text = day.date.dayOfMonth.toString()
                textView.visibility = View.VISIBLE
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PT, 12F)

                val shape = GradientDrawable()
                shape.cornerRadius = 20F
                shape.setColor(Color.LTGRAY)


                if (day.owner == DayOwner.THIS_MONTH) {
                    val isThereEvent = events.any { event -> isSameDate(event.date, day.date) }
                    val isOpened = day.date.dayOfWeek.ordinal == 5 || isThereEvent
                    val isFuture =
                        day.date.isAfter(LocalDate.now()) || day.date.isEqual(LocalDate.now())
                    if (day.date == container.selectedDate && isOpened && isFuture) {
                        selectedDate = day
                        viewTable.isEnabled = true
                        if (amountOfTableTicket > 0 || amountOfSimpleTicket > 0)
                            payButton.isEnabled = true

                        lastHighlighted?.setTextColor(if (lastHighlighted?.tag == true) Color.RED else Color.BLACK)
                        lastHighlighted?.background = null

                        textView.setTypeface(null, Typeface.BOLD)
                        textView.setTextColor(Color.WHITE)
                        textView.background = shape
                        textView.tag = isThereEvent
                        lastHighlighted = textView
                        if (isThereEvent)
                            showEvent(events.first { event -> isSameDate(event.date, day.date) })
                        else
                            showEvent(null)
                    } else if (isThereEvent) {
                        textView.setTypeface(null, Typeface.BOLD)
                        textView.setTextColor(Color.RED)
                        textView.background = null
                    } else if (!isOpened || !isFuture) {
                        textView.setTextColor(Color.GRAY)
                        textView.background = null
                    } else {
                        textView.setTextColor(Color.BLACK)
                        textView.setTypeface(null, Typeface.BOLD)
                        textView.background = null
                    }
                } else {
                    textView.setTextColor(Color.LTGRAY)
                }
            }
        }

        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                container.textView.text =
                    month.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        .replaceFirstChar(Char::uppercase) + " " + month.year
            }
        }

        if (event != null) {
            findViewById<TextView>(R.id.BuyTicketCalendarAction).visibility = View.GONE
            findViewById<LinearLayout>(R.id.BuyTicketCalendarLegend).visibility = View.GONE
            findViewById<CardView>(R.id.BuyTicketCalendarCard).visibility = View.GONE
        }

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = daysOfWeekFromLocale()
        val locale = Locale.getDefault()
        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek.first())
        calendarView.scrollToMonth(currentMonth)

        val dayOfWeekLLayout = findViewById<LinearLayout>(R.id.BuyTicketDayOfWeekLLayout)
        firstDayOfWeek.forEach { e ->
            val textView = TextView(this)
            textView.text =
                e.getDisplayName(TextStyle.SHORT, locale).replaceFirstChar(Char::uppercase)
            val param = LinearLayout.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                1.0f
            )
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            textView.layoutParams = param
            textView.setTypeface(null, Typeface.BOLD)
            dayOfWeekLLayout.addView(textView)
        }

        viewTable.setOnClickListener {
            val intent = Intent(this, SelectTable::class.java)
            intent.putExtra("club", club)
            intent.putExtra("date", selectedDate!!.date.toString())
            if (this.amountOfTableTicket > 0) {
                intent.putExtra("showMode", false)
                intent.putExtra("simpleTicket", amountOfSimpleTicket)
                intent.putExtra("tableTicket", amountOfTableTicket)
            } else
                intent.putExtra("showMode", true)
            startActivity(intent)
        }
        payButton.setOnClickListener {
            if (User.isLogged(this)) {
                var formatter = SimpleDateFormat("yyyy-MM-dd")
                if (amountOfTableTicket > 0) {
                    val intent = Intent(this, SelectTable::class.java)
                    intent.putExtra("club", club)
                    intent.putExtra("showMode", false)
                    intent.putExtra("simpleTicket", amountOfSimpleTicket)
                    intent.putExtra("tableTicket", amountOfTableTicket)
                    if (event == null)
                        intent.putExtra("date", selectedDate?.date.toString())
                    else
                        intent.putExtra("date", formatter.format(event!!.date))
                    startActivity(intent)
                } else {
                    val order = Order()
                    val orderItem0 =
                        OrderItem(
                            "Ingresso semplice",
                            amountOfSimpleTicket,
                            club!!.simpleTicketPrice
                        )
                    val orderItem1 =
                        OrderItem(
                            "Ingresso con tavolo",
                            amountOfTableTicket,
                            club!!.tableTicketPrice
                        )
                    order.tickets.add(orderItem0)
                    order.tickets.add(orderItem1)
                    if (event == null) //2022-09-24
                        order.date = selectedDate?.date.toString()
                    else
                        order.date = formatter.format(event!!.date)
                    order.club = club
                    val intent = Intent(applicationContext, Payment::class.java)
                    intent.putExtra("OrderPreview", order)
                    startActivity(intent)
                }
            } else {
                User.generateNotLoggedAlertDialog(this).show()
            }
        }
    }

    private fun isSameDate(date: Date, localDate: LocalDate): Boolean {
        val d = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        return d.dayOfYear == localDate.dayOfYear && d.year == localDate.year
    }

    private fun showEvent(event: Event?) {
        val eventLayout = findViewById<FrameLayout>(R.id.BuyTicketEventCardLayout)
        eventLayout.removeAllViews()
        eventLayout.visibility = View.GONE
        if (event != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(eventLayout.id, EventElement.newInstance(event, marginless = true))
            transaction.commit()
            eventLayout.visibility = View.VISIBLE
        }
    }

    fun updateCartTotal() {
        val payButton = findViewById<Button>(R.id.BuyTicketPayButton)
        val viewTable = findViewById<Button>(R.id.BuyTicketViewTableButton)
        val totalAmountView = findViewById<TextView>(R.id.BuyTicketTotalAmount)
        if (this.amountOfTableTicket > 0) {
            viewTable.visibility = View.GONE
            payButton.text = "Seleziona tavoli"
        } else {
            viewTable.visibility = View.VISIBLE
            payButton.text = "Pagamento"
        }
        club?.let {
            val amount =
                it.simpleTicketPrice * amountOfSimpleTicket + it.tableTicketPrice * amountOfTableTicket
            totalAmountView.text = String.format("%.2f", amount).plus("€")
        }

        payButton.isEnabled =
            (amountOfTableTicket > 0 || amountOfSimpleTicket > 0) && (event != null || selectedDate != null)
    }

    fun daysOfWeekFromLocale(): Array<DayOfWeek> {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        val daysOfWeek = DayOfWeek.values()
        // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
        // Only necessary if firstDayOfWeek is not DayOfWeek.MONDAY which has ordinal 0.
        if (firstDayOfWeek != DayOfWeek.MONDAY) {
            val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
            val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
            return rhs + lhs
        }
        return daysOfWeek
    }

}