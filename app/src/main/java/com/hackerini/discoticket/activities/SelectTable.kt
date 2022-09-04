package com.hackerini.discoticket.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.views.ClubMap
import com.hackerini.discoticket.objects.Club
import kotlin.math.ceil

class SelectTable : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_table)
        val club = intent.getSerializableExtra("club") as Club
        val tableTickets = intent.getIntExtra("tableTicket", 15)
        val isShowMode = intent.getBooleanExtra("showMode", true)

        val titleTextView = findViewById<TextView>(R.id.SelectTableClubName)
        val addressTextView = findViewById<TextView>(R.id.SelectTableClubAddress)
        val actionTitle = findViewById<TextView>(R.id.SelectTableActionTitle)
        val remainderOfSeatsTextView = findViewById<TextView>(R.id.SelectTableRemainRecapt)
        val actionRecapTextView = findViewById<TextView>(R.id.SelectTableActionRecapt)
        val successIconImageView = findViewById<ImageView>(R.id.SelectTableSuccessIcon)
        val payButton = findViewById<Button>(R.id.SelectTablePayButton)

        payButton.isEnabled = false
        titleTextView.text = club.name
        addressTextView.text = club.address
        actionRecapTextView.text = "Stai prenotando per un totale di ${tableTickets} persone"
        if (tableTickets > 8)
            remainderOfSeatsTextView.text = "Seleziona più tavoli per avere ${tableTickets} sedute"
        else
            remainderOfSeatsTextView.text = "Seleziona un tavolo con almeno ${tableTickets} sedute"

        if (tableTickets > 8) {
            actionTitle.text = "Seleziona i tavoli da prenotare"
        } else {
            actionTitle.text = "Seleziona il tavolo da prenotare"
        }

        val llMap = findViewById<LinearLayout>(R.id.SelectTableLinearLayoutMap)
        val clubMap = ClubMap(this)
        clubMap.setOnTableClickListener { tables ->
            val bookedSeats = tables.map { table -> table.numberOfSets }.sum()
            if (bookedSeats >= tableTickets) {
                //Selection complete
                successIconImageView.setImageResource(R.drawable.ic_baseline_check_24)
                remainderOfSeatsTextView.text =
                    "Hai selezionato un numero di tavoli sufficenti per avere ${tableTickets} sedute"
                payButton.isEnabled = true
            } else {
                successIconImageView.setImageResource(R.drawable.ic_baseline_close_24)
                val remainder = tableTickets - bookedSeats
                if (remainder > 8)
                    remainderOfSeatsTextView.text =
                        "Seleziona più tavoli per avere ${remainder} sedute"
                else
                    remainderOfSeatsTextView.text =
                        "Seleziona un tavolo con almeno ${remainder} sedute"
                payButton.isEnabled = false
            }
        }
        llMap.addView(clubMap)

        val tableToSelect = tableTickets.toFloat() / 8F
        clubMap.selectableTables = ceil(tableToSelect).toInt()

        if (isShowMode) {
            payButton.visibility = View.GONE
            findViewById<TextView>(R.id.SelectTableActionTitle).visibility=View.GONE
            findViewById<TextView>(R.id.SelectTableExplain).visibility=View.GONE
            actionRecapTextView.visibility=View.GONE
            remainderOfSeatsTextView.visibility=View.GONE
            successIconImageView.visibility=View.GONE
            clubMap.isShowMode = isShowMode

        }
    }
}