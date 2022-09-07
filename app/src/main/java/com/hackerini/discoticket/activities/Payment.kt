package com.hackerini.discoticket.activities

import android.content.Intent
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.hackerini.discoticket.MainActivity
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.Discount
import com.hackerini.discoticket.objects.Order
import com.hackerini.discoticket.objects.OrderWithOrderItem
import com.hackerini.discoticket.objects.TypeOfDiscount
import com.hackerini.discoticket.room.RoomManager

class Payment : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    val spinnerItems = listOf(
        Discount("Non utilizzare nessuno sconto", 0F, TypeOfDiscount.Nothing),
        Discount("Un drink gratis", 1F, TypeOfDiscount.FreeDrink),
        Discount("Sconto del 20%", 0.2F, TypeOfDiscount.Percentage),
    )    //DOVREBBE ESSERCI UNA FUNZIONE APPOSTA

    var order: Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        order = intent.getSerializableExtra("OrderPreview") as Order

        //Retrieve the items to purchase and populate the list
        val purchaseList = findViewById<LinearLayout>(R.id.paymentList)
        order?.getAllElements()?.forEach { e ->
            if (e.quantity > 0) {
                val listElement = TextView(this)
                listElement.text = " " + e.quantity.toString() + "x " + e.name + " - " +
                        String.format("%.2f", e.getTotalAmount()) + "€"
                listElement.textSize = 21f
                listElement.addBorder()
                purchaseList.addView(listElement)
            }
        }

        //Create dropdown menu for discounts
        val dropdown = findViewById<Spinner>(R.id.paymentSpinner)
        val dataAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                spinnerItems.map { e -> e.name })
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)      //Layout style - list view with radio button
        dropdown.adapter = dataAdapter
        dropdown.onItemSelectedListener = this


        //Question mark popup
        val questionMark = findViewById<ImageView>(R.id.paymentQuestionMark)
        questionMark.setOnClickListener {
            val alert = AlertDialog.Builder(this).create()
            alert.setMessage("Puoi selezionare uno sconto, se ne hai uno disponibile; in alternativa, puoi anche inserire un codice coupon")
            alert.show()                    //COME FACCIO A FAR SCRIVERE MANUALMENTE??
        }

        //Write the total amount
        val totalAmount =
            findViewById<TextView>(R.id.paymentTotal)
        totalAmount.text = String.format("%.2f", order?.getTotalAmount()).plus("€")

        //Create alert on button press
        val button = findViewById<Button>(R.id.paymentButton)
        button.setOnClickListener {
            val orderPreview = storeOrder()

            val alert = AlertDialog.Builder(this).create()
            alert.setCancelable(false)
            alert.setTitle("L'acquisto è andato a buon fine!")
            alert.setButton(
                AlertDialog.BUTTON_NEGATIVE,
                "Torna alla\nhomepage"
            ) { dialog, _ -> dialog.dismiss() }
            alert.setButton(
                AlertDialog.BUTTON_POSITIVE,
                "Mostra\ncodice QR"
            ) { dialog, _ -> dialog.dismiss() }
            alert.show()

            val buttonNegative = alert.getButton(AlertDialog.BUTTON_NEGATIVE)
            val buttonPositive = alert.getButton(AlertDialog.BUTTON_POSITIVE)
            val layoutParams = buttonNegative.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = 10f
            buttonNegative.layoutParams = layoutParams
            buttonPositive.layoutParams = layoutParams
            val buttonHeight = 200
            buttonNegative.height = buttonHeight
            buttonPositive.height = buttonHeight

            buttonNegative.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)     //Back to home
                startActivity(intent)
            }
            buttonPositive.setOnClickListener {
                val intent = Intent(this, QRdrinks::class.java)         //Show QR
                intent.putExtra("orderPreview", orderPreview)
                startActivity(intent)
            }
        }
    }

    private fun storeOrder(): OrderWithOrderItem {
        val roomManager = RoomManager(this)
        val dao = roomManager.db.orderDao()
        val itemDao = roomManager.db.orderItemDao()
        order?.prepare()
        dao.insert(order!!)
        val lastId = dao.getLastId()

        if (order?.tickets?.isNotEmpty() == true) {
            order?.tickets?.forEach { orderItem ->
                orderItem.orderId = lastId
                itemDao.insert(orderItem)
            }
        } else if (order?.drinks?.isNotEmpty() == true) {
            order?.drinks?.forEach { orderItem ->
                orderItem.orderId = lastId
                itemDao.insert(orderItem)
            }
        }
        return dao.getOrderWithOrderItem(lastId)
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val totalAmount = findViewById<TextView>(R.id.paymentTotal)
        val totalAmountNotDiscounted = findViewById<TextView>(R.id.paymentPreTotal)
        val totalAmountNotDiscountedText = findViewById<TextView>(R.id.paymentPreTotalText)
        val discount = findViewById<TextView>(R.id.paymentDiscount)
        val discountText = findViewById<TextView>(R.id.paymentDiscountText)
        val errorText = findViewById<TextView>(R.id.paymentCouponError)

        val selectedDiscount = spinnerItems[position]
        totalAmountNotDiscounted.text =
            String.format("%.2f", order?.getTotalAmount()).plus("€")

        totalAmountNotDiscounted.visibility = View.GONE
        totalAmountNotDiscountedText.visibility = View.GONE
        discountText.visibility = View.GONE
        discount.visibility = View.GONE
        totalAmount.text = String.format("%.2f", order?.getTotalAmount()!!).plus("€")

        if (selectedDiscount.type == TypeOfDiscount.Percentage) {
            val discountAmount = selectedDiscount.amount * order?.getTotalAmount()!!
            discount.text = "-".plus(String.format("%.2f", discountAmount)).plus("€")
            totalAmount.text =
                String.format("%.2f", order?.getTotalAmount()!! - discountAmount)
                    .plus("€")
        } else if (selectedDiscount.type == TypeOfDiscount.FreeDrink) {
            if (order?.drinks?.isNotEmpty() == true) {
                val discountAmount =
                    order?.drinks?.minBy { e -> e.unitaryPrice }?.unitaryPrice ?: 0f
                discount.text = "-".plus(String.format("%.2f", discountAmount)).plus("€")
                totalAmount.text =
                    String.format("%.2f", order?.getTotalAmount()!! - discountAmount)
                        .plus("€")
            } else {
                errorText.text = "Copuon non valido per questo ordine"
                errorText.visibility = View.VISIBLE
                totalAmount.text = String.format("%.2f", order?.getTotalAmount()!!).plus("€")
            }
        }

        val isSelectionValid =
            selectedDiscount.type == TypeOfDiscount.Percentage || (selectedDiscount.type == TypeOfDiscount.FreeDrink && (order?.drinks?.isNotEmpty() == true))
        if (isSelectionValid) {
            totalAmountNotDiscounted.visibility = View.VISIBLE
            totalAmountNotDiscountedText.visibility = View.VISIBLE
            discountText.visibility = View.VISIBLE
            discount.visibility = View.VISIBLE
            errorText.visibility = View.GONE
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}