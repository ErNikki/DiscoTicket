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
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hackerini.discoticket.MainActivity
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.*
import com.hackerini.discoticket.room.RoomManager
import java.lang.Float.min
import java.util.*

class Payment : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var order: Order? = null
    private var coupons: List<Discount> = LinkedList()
    private lateinit var creditCard: CardView
    private lateinit var googlePay: CardView
    private lateinit var payPal: CardView
    var isPaymentSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        order = intent.getSerializableExtra("OrderPreview") as Order
        creditCard = findViewById(R.id.paymentCreditCard)
        googlePay = findViewById(R.id.paymentGooglePay)
        payPal = findViewById(R.id.paymentPayPal)

        creditCard.setOnClickListener { onPaymentMethodClick(it) }
        googlePay.setOnClickListener { onPaymentMethodClick(it) }
        payPal.setOnClickListener { onPaymentMethodClick(it) }

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

        if (User.isLogged(this)) {
            val userId = User.getLoggedUser(this)!!.id
            coupons = RoomManager(this).db.userDao().getUserDiscount(userId).first().items
            val mutableList = coupons.toMutableList()
            mutableList.add(
                0,
                Discount("Non utilizzare nessuno sconto", 0F, TypeOfDiscount.Nothing)
            )
            coupons = mutableList.toList()
        }

        //Create dropdown menu for discounts
        val dropdown = findViewById<Spinner>(R.id.paymentSpinner)
        val dataAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                coupons.map { e -> e.name })
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)      //Layout style - list view with radio button
        dropdown.adapter = dataAdapter
        dropdown.onItemSelectedListener = this


        //Question mark popup
        val questionMark = findViewById<ImageView>(R.id.paymentQuestionMark)
        questionMark.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setMessage("Puoi selezionare uno sconto da applicare al tuo acquisto, se ne hai uno disponibile")
            builder.setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
            builder.create().show()
        }

        //Write the total amount
        val totalAmount =
            findViewById<TextView>(R.id.paymentTotal)
        totalAmount.text = String.format("%.2f", order?.getTotalAmount()).plus("€")

        //Create alert on button press
        val button = findViewById<Button>(R.id.paymentButton)
        button.setOnClickListener {
            if (!isPaymentSelected) {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setCancelable(false)
                builder.setTitle("Errore")
                builder.setMessage("Seleziona un metodo di pagamento")
                builder.setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                builder.create().show()
                return@setOnClickListener
            }
            val orderPreview = storeOrder()

            //Update points
            val userDao = RoomManager(this).db.userDao()
            val earnedPoints =
                (ResourcesCompat.getFloat(resources, R.dimen.euroToPoint)
                        * orderPreview.getTotalAmount()).toInt()
            userDao.incrementsPoints(earnedPoints, User.getLoggedUser(this)!!.id)

            val builder = MaterialAlertDialogBuilder(this)
            builder.setCancelable(false)
            builder.setTitle("L'acquisto è andato a buon fine!")
            builder.setMessage(
                "Complementi! Hai guadagnato ${earnedPoints} punti che potrai usare per ottenere sconti.\n\n" +
                        "Puoi trovare il QR Code successivamente nella sezione \"Cronologia acquisti\" nel menù laterale."
            )
            builder.setNegativeButton(
                "Torna alla\nhomepage"
            ) { dialog, _ -> dialog.dismiss() }
            builder.setPositiveButton(
                "Mostra\ncodice QR"
            ) { dialog, _ -> dialog.dismiss() }

            val alert = builder.create()
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
                intent.putExtra("order", orderPreview)
                startActivity(intent)
            }
        }
    }

    private fun storeOrder(): OrderWithOrderItem {
        val roomManager = RoomManager(this)
        val dao = roomManager.db.orderDao()
        val itemDao = roomManager.db.orderItemDao()
        val discountDao = roomManager.db.discountDao()
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
        order?.appliedDiscount?.let { discountDao.delete(it) }
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
        val totalAmountTextView = findViewById<TextView>(R.id.paymentTotal)
        val totalAmountNotDiscountedTextView = findViewById<TextView>(R.id.paymentPreTotal)
        val totalAmountNotDiscountedText = findViewById<TextView>(R.id.paymentPreTotalText)
        val discountAmountTextView = findViewById<TextView>(R.id.paymentDiscount)
        val discountText = findViewById<TextView>(R.id.paymentDiscountText)
        val errorText = findViewById<TextView>(R.id.paymentCouponError)
        val pointPreview = findViewById<TextView>(R.id.paymentPointsPreview)

        val selectedDiscount = coupons[position]
        totalAmountNotDiscountedTextView.text =
            String.format("%.2f", order?.getTotalAmount()).plus("€")

        totalAmountNotDiscountedTextView.visibility = View.GONE
        totalAmountNotDiscountedText.visibility = View.GONE
        discountText.visibility = View.GONE
        discountAmountTextView.visibility = View.GONE
        totalAmountTextView.text = String.format("%.2f", order?.getTotalAmount()!!).plus("€")

        when (selectedDiscount.type) {
            TypeOfDiscount.Percentage -> order?.discount =
                selectedDiscount.amount * order?.getTotalAmount()!!
            TypeOfDiscount.NeatValue -> order?.discount =
                min(selectedDiscount.amount, order!!.getTotalAmount())
            TypeOfDiscount.FreeDrink -> {
                if (order?.drinks?.isNotEmpty() == true) {
                    order?.discount =
                        order?.drinks?.minBy { e -> e.unitaryPrice }?.unitaryPrice ?: 0f
                } else {
                    order?.discount = 0F
                    errorText.text = "Copuon non valido per questo ordine"
                    errorText.visibility = View.VISIBLE
                }
            }
            TypeOfDiscount.Nothing -> {
                errorText.visibility = View.GONE
                order?.discount = 0F
            }
        }

        discountAmountTextView.text = "-".plus(String.format("%.2f", order?.discount)).plus("€")
        val discountedAmount = order!!.getTotalAmount() - order?.discount!!
        totalAmountTextView.text =
            String.format("%.2f", discountedAmount).plus("€")
        val pointThatWillEarn =
            (ResourcesCompat.getFloat(resources, R.dimen.euroToPoint) * discountedAmount).toInt()
        pointPreview.text =
            "Completando questo ordine otterrai ".plus(pointThatWillEarn).plus(" punti")
        order?.appliedDiscount = selectedDiscount

        val isSelectionValid =
            selectedDiscount.type == TypeOfDiscount.Percentage ||
                    selectedDiscount.type == TypeOfDiscount.NeatValue ||
                    (selectedDiscount.type == TypeOfDiscount.FreeDrink && (order?.drinks?.isNotEmpty() == true))
        if (isSelectionValid) {
            totalAmountNotDiscountedTextView.visibility = View.VISIBLE
            totalAmountNotDiscountedText.visibility = View.VISIBLE
            discountText.visibility = View.VISIBLE
            discountAmountTextView.visibility = View.VISIBLE
            errorText.visibility = View.GONE
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    private fun onPaymentMethodClick(view: View) {
        isPaymentSelected = true
        when (view.id) {
            payPal.id -> {
                payPal.backgroundTintList = ContextCompat.getColorStateList(this, R.color.clubColor)
                googlePay.backgroundTintList = null
                creditCard.backgroundTintList = null
            }
            googlePay.id -> {
                payPal.backgroundTintList = null
                googlePay.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.clubColor)
                creditCard.backgroundTintList = null
            }
            creditCard.id -> {
                payPal.backgroundTintList = null
                googlePay.backgroundTintList = null
                creditCard.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.clubColor)
            }
        }
    }
}