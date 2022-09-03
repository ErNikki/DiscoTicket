package com.hackerini.discoticket.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.views.ClubMap

class SelectTable : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_table)

        val llMap = findViewById<LinearLayout>(R.id.SelectTableLinearLayoutMap)
        val clubMap = ClubMap(this)
        llMap.addView(clubMap)

        clubMap.setOnClickListener {  }

    }
}