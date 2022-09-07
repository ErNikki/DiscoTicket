package com.hackerini.discoticket.activities

import android.app.ActionBar
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.views.DayViewContainer
import com.hackerini.discoticket.fragments.views.MonthViewContainer
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.OrderItem
import com.hackerini.discoticket.objects.OrderPreview
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*

class BuyTicket : AppCompatActivity() {

    private var selectedDate: CalendarDay? = null
    private var amountOfSimpleTicket = 0
    private var amountOfTableTicket = 0
    private var club: Club? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_ticket)

        club = intent.getSerializableExtra("club") as Club

        val clubName = findViewById<TextView>(R.id.BuyTicketClubName)
        val clubAddress = findViewById<TextView>(R.id.BuyTicketClubAddress)
        val simpleTicketPrice = findViewById<TextView>(R.id.BuyTicketSimpleTicketPrice)
        val tableTicketPrice = findViewById<TextView>(R.id.BuyTicketTableTicketPrice)
        val payButton = findViewById<Button>(R.id.BuyTicketPayButton)
        val viewTable = findViewById<Button>(R.id.BuyTicketViewTableButton)
        val simpleTicketCounter = findViewById<EditText>(R.id.BuyTicketAmountSimpleTicket)
        val tableTicketCounter = findViewById<EditText>(R.id.BuyTicketAmountTableTicket)

        clubName.text = club?.name
        clubAddress.text = club?.address
        simpleTicketPrice.text = String.format("%.2f", club?.simpleTicketPrice) + "€/Persona"
        tableTicketPrice.text = String.format("%.2f", club?.tableTicketPrice) + "€/Persona"

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

                val shape = GradientDrawable()
                shape.cornerRadius = 20F
                shape.setColor(Color.LTGRAY)


                if (day.owner == DayOwner.THIS_MONTH) {
                    val isOpened = day.date.dayOfWeek.ordinal == 5
                    val isFuture =
                        day.date.isAfter(LocalDate.now()) || day.date.isEqual(LocalDate.now())
                    if (day.date == container.selectedDate && isOpened && isFuture) {
                        selectedDate = day
                        if (amountOfTableTicket > 0 || amountOfSimpleTicket > 0)
                            payButton.isEnabled = true
                        lastHighlighted?.setTextColor(Color.BLACK)
                        lastHighlighted?.background = null

                        textView.setTextColor(Color.WHITE)
                        textView.background = shape
                        lastHighlighted = textView
                    } else if (!isOpened || !isFuture) {
                        textView.setTextColor(Color.GRAY)
                        textView.background = null
                    } else {
                        textView.setTextColor(Color.BLACK)
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
            val club = Club()
            club.name = "Discoteca prova"
            club.address = "Via del corso, 43, Roma"
            val intent = Intent(this, SelectTable::class.java)
            intent.putExtra("club", club)
            if (this.amountOfTableTicket > 0) {
                intent.putExtra("showMode", false)
                intent.putExtra("simpleTicket", amountOfSimpleTicket)
                intent.putExtra("tableTicket", amountOfTableTicket)
            } else
                intent.putExtra("showMode", true)
            startActivity(intent)
        }
        payButton.setOnClickListener {
            if (amountOfTableTicket > 0) {
                val intent = Intent(this, SelectTable::class.java)
                intent.putExtra("club", club)
                intent.putExtra("showMode", false)
                intent.putExtra("simpleTicket", amountOfSimpleTicket)
                intent.putExtra("tableTicket", amountOfTableTicket)
                startActivity(intent)
            } else {
                val orderPreview = OrderPreview()
                val orderItem0 =
                    OrderItem("Ingresso semplice", amountOfSimpleTicket, club!!.simpleTicketPrice)
                val orderItem1 =
                    OrderItem("Ingresso con tavolo", amountOfTableTicket, club!!.tableTicketPrice)
                orderPreview.tickets.add(orderItem0)
                orderPreview.tickets.add(orderItem1)
                val intent = Intent(applicationContext, Payment::class.java)
                intent.putExtra("OrderPreview", orderPreview)
                startActivity(intent)
            }
        }
        simpleTicketCounter.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                amountOfSimpleTicket = s.toString().toIntOrNull() ?: 0
                updateCartTotal()
            }
        })
        tableTicketCounter.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                amountOfTableTicket = s.toString().toIntOrNull() ?: 0
                updateCartTotal()
            }
        })
    }

    fun updateCartTotal() {
        val payButton = findViewById<Button>(R.id.BuyTicketPayButton)
        val viewTable = findViewById<Button>(R.id.BuyTicketViewTableButton)
        val totalAmountView = findViewById<TextView>(R.id.BuyTicketTotalAmount)
        if (this.amountOfTableTicket > 0) {
            viewTable.visibility = View.GONE
            payButton.text = "Seleziona tavolo"
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
            (amountOfTableTicket > 0 || amountOfSimpleTicket > 0) && selectedDate != null
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