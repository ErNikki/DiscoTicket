package com.hackerini.discoticket.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hackerini.discoticket.MainActivity
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.OrderWithOrderItem

class QRdrinks : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_drinks)

        val orderPreview = intent.getSerializableExtra("order") as OrderWithOrderItem?
        val message = findViewById<TextView>(R.id.QrCodeMessage)

        if (orderPreview?.includeTickets() == true) {
            message.text =
                "Mostra questo codice QR al personale all'ingresso: lo scannerizzerà e ti farà entrare!"
        }

        val button = findViewById<Button>(R.id.qrDrinksButton)
        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}