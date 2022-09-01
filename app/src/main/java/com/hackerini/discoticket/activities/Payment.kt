package com.hackerini.discoticket.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.OrderPreview

class Payment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        val orderPreview = intent.getSerializableExtra("OrderPreview") as OrderPreview

        orderPreview.items.forEach { e ->
            Log.d("TAG", e.name)
            /*For each element {e} you have:
            e.name
            e.quantity
            e.unitaryPrice //If needed
            e.getTotalAmount() //To get the total amount of each drink
             */
        }

        orderPreview.getTotalAmount() //Computes the total amount of the order
    }
}