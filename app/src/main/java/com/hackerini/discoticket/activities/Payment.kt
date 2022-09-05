package com.hackerini.discoticket.activities

import android.content.Intent
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.hackerini.discoticket.MainActivity
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.OrderPreview

class Payment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        //Add border to the box containing the purchase list
        val listBox = findViewById<ScrollView>(R.id.paymentDetails)
        listBox.addBorder()

        //Retrieve the items to purchase and populate the list
        val orderPreview = intent.getSerializableExtra("OrderPreview") as OrderPreview
        val purchaseList = findViewById<LinearLayout>(R.id.paymentList)
        orderPreview.items.forEach { e ->
            val listElement = TextView(this)
            listElement.text = " " + e.quantity.toString() + "x " + e.name + " - " +
                                String.format("%.2f", e.getTotalAmount()) + "€"
            listElement.textSize = 21f
            listElement.addBorder()
            purchaseList.addView(listElement)
        }

        //Create dropdown menu for discounts
        val dropdown = findViewById<Spinner>(R.id.paymentSpinner) as Spinner
        val items = listOf("Non utilizzare nessuno sconto", "Un drink gratis", "Sconto del 20%")    //DOVREBBE ESSERCI UNA FUNZIONE APPOSTA
        val dataAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)      //Layout style - list view with radio button
        dropdown.adapter = dataAdapter

        //Question mark popup
        val questionMark = findViewById<ImageView>(R.id.paymentQuestionMark) as ImageView
        questionMark.setOnClickListener {
            Log.d("TAG","HEHEEHEHEHH")
            val text = TextView(this)
            text.textSize = 20f
            text.text = "AAAAAAAAAAAAA"
            //questionMark.addView(text)
            //val layoutParams = questionMark.layoutParams as RelativeLayout.LayoutParams
            //val paymentHelp = findViewById<TextView>(R.id.paymentHelp)
            //paymentHelp.layoutParams = layoutParams

            /*val position = IntArray(2)
            questionMark.getLocationOnScreen(position)
            Log.d("TAG", position[0].toString() + " " + position[1])
            val help = findViewById<RelativeLayout>(R.id.paymentRelativeLayout)
            help.x = position[0].toFloat()
            help.y = position[1].toFloat()
            Log.d("TAG", help.x.toString() + " " + help.y.toString())*/

            val alert = AlertDialog.Builder(this).create()
            //alert.setCancelable(false)
            alert.setMessage("Puoi selezionare uno sconto, se ne hai uno disponibile; in alternativa, puoi anche inserire un codice coupon")
            alert.show()
        }

        //Write the total amount
        val totalAmount = findViewById<TextView>(R.id.paymentTotal)                         //DOVREBBE AGGIORNARSI CON GLI SCONTI
        totalAmount.append(String.format("%.2f", orderPreview.getTotalAmount()) + "€")

        //Create alert on button press
        val button = findViewById<Button>(R.id.paymentButton) as Button
        button.setOnClickListener {
            val alert = AlertDialog.Builder(this).create()
            alert.setCancelable(false)
            alert.setTitle("L'acquisto è andato a buon fine!")
            alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Torna alla\nhomepage") { dialog, _ -> dialog.dismiss() }
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "Mostra\ncodice QR") { dialog, _ -> dialog.dismiss() }
            alert.show()

            val buttonNegative = alert.getButton(AlertDialog.BUTTON_NEGATIVE)
            val buttonPositive = alert.getButton(AlertDialog.BUTTON_POSITIVE)
            val layoutParams = buttonNegative.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = 10f
            buttonNegative.layoutParams = layoutParams
            buttonPositive.layoutParams = layoutParams
            val buttonHeight = 170
            buttonNegative.height = buttonHeight
            buttonPositive.height = buttonHeight

            buttonNegative.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)     //Back to home
                startActivity(intent)
            }
            buttonPositive.setOnClickListener {
                val intent = Intent(this, QRdrinks::class.java)         //Show QR
                startActivity(intent)
            }
        }
    }

    //Function for adding a border to the list box and to the elements of the list
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
}