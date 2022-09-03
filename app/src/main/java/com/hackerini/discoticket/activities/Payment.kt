package com.hackerini.discoticket.activities

import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.OrderPreview

class Payment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        findViewById<ScrollView>(R.id.paymentDetails).addBorder()          //Add border to the list container

        val orderPreview = intent.getSerializableExtra("OrderPreview") as OrderPreview
        val purchaseList = findViewById<LinearLayout>(R.id.paymentList)
        orderPreview.items.forEach { e ->                   //Add elements about to be purchased to the list
            val listElement = TextView(this)
            listElement.text = " " + e.quantity.toString() + "x " + e.name + " - " +
                                String.format("%.2f", e.getTotalAmount()) + "€"
            listElement.textSize = 20f
            listElement.addBorder()
            purchaseList.addView(listElement)
        }

        findViewById<TextView>(R.id.paymentTotal).append(String.format("%.2f", orderPreview.getTotalAmount()) + "€")

        val button = findViewById<Button>(R.id.paymentButton) as Button
        button.setOnClickListener {
            Log.d("LOG","AAAAAA")
            val alert = AlertDialog.Builder(this).create()
            alert.setCancelable(false)
            alert.setTitle("L'acquisto è andato a buon fine!")
            alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Homepage") { dialog, _ -> dialog.dismiss() }
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "QR") { dialog, _ -> dialog.dismiss() }
            alert.show()

            val btnNegative = alert.getButton(AlertDialog.BUTTON_NEGATIVE)
            val btnPositive = alert.getButton(AlertDialog.BUTTON_POSITIVE)
            val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = 10f
            btnNegative.layoutParams = layoutParams
            btnPositive.layoutParams = layoutParams
        }
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