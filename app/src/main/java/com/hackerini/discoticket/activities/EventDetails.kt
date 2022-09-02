package com.hackerini.discoticket.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.Event

class EventDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        val event = intent.getSerializableExtra("event") as Event
    }
}