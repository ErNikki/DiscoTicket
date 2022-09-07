package com.hackerini.discoticket.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.Club

class WriteReview : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_review)

        val club = intent.getSerializableExtra("club") as Club

    }
}