package com.hackerini.discoticket.activities

import android.app.ActionBar
import android.content.Intent
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Event
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class EventDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        val event = intent.getSerializableExtra("event") as Event
        Club.addToLastSeen(this, event.id, Event::class)
        val df = SimpleDateFormat("dd/MM/YYYY", Locale.getDefault())

        val eventImage = findViewById<ImageView>(R.id.eventDetaileventImage)
        val eventName = findViewById<TextView>(R.id.eventDetailseventName)
        val address = findViewById<TextView>(R.id.eventDetailsAddress)
        val clubName = findViewById<TextView>(R.id.eventDetailClubName)
        val eventDate = findViewById<TextView>(R.id.eventDetailsDate)
        val eventDescription = findViewById<TextView>(R.id.eventDeatilseventDescription)
        val tagLayout = findViewById<LinearLayout>(R.id.eventDetailsTagLayout)

        clubName.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        eventName.text = event.name
        address.text = event.club?.address
        eventDate.text = df.format(event.date)
        clubName.text = event.club?.name
        eventDescription.text = event.description
        val imageSize = 250
        Picasso.get().load(event.imgUrl).resize(imageSize, imageSize).into(eventImage)

        findViewById<Button>(R.id.eventDetailsDrinkMenuButton).setOnClickListener {
            val intent = Intent(this, DrinkMenu::class.java)
            intent.putExtra("club", event.club)
            startActivity(intent)
        }

        findViewById<Button>(R.id.eventDetailsBuyTicketsButton).setOnClickListener {
            val intent = Intent(this, BuyTicket::class.java)
            intent.putExtra("club", event.club)
            intent.putExtra("event", event)
            startActivity(intent)
        }

        clubName.setOnClickListener {
            val intent = Intent(this, ClubDetails::class.java)
            intent.putExtra("club", event.club)
            startActivity(intent)
        }

        val params =
            LinearLayout.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            )
        params.setMargins(0, 0, 10, 0)

        event.labels.forEach { e ->
            val shape = GradientDrawable()
            shape.cornerRadius = 10f
            shape.setColor(Club.getLabelColorFromName(e))

            val textview = TextView(this)
            textview.text = e
            textview.background = shape
            textview.setPadding(8)
            textview.layoutParams = params
            textview.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tagLayout.addView(textview)
        }
    }
}