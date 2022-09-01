package com.hackerini.discoticket.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.Club

class ClubDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_details)

        val club = intent.getSerializableExtra("club") as Club
        Log.d("TAG",club.name)
    }
}