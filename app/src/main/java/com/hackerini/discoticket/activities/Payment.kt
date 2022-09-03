package com.hackerini.discoticket.activities

import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.OrderPreview

class Payment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        findViewById<ScrollView>(R.id.purchaseDetails).addBorder()          //Add border to the list container

        val orderPreview = intent.getSerializableExtra("OrderPreview") as OrderPreview
        val purchaseList = findViewById<LinearLayout>(R.id.purchaseList)
        orderPreview.items.forEach { e ->                   //Add elements about to be purchased to the list
            val listElement = TextView(this)
            listElement.text = " " + e.quantity.toString() + "x " + e.name + " - " +
                                String.format("%.2f", e.getTotalAmount()) + "€"
            listElement.textSize = 20f
            listElement.addBorder()
            purchaseList.addView(listElement)
        }

        findViewById<TextView>(R.id.purchaseTotal).append(String.format("%.2f", orderPreview.getTotalAmount()) + "€")
    }
}

//Function for adding a border to the list container and to the elements of the list
fun View.addBorder() {
    val drawable = ShapeDrawable().apply {
        shape = RectShape()
        paint.apply {
            strokeWidth = 5F                //Border width
            style = Paint.Style.STROKE
        }
    }
    background = drawable
}