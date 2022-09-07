package com.hackerini.discoticket.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.DrinkElement
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.ItemType
import com.hackerini.discoticket.objects.Order
import com.hackerini.discoticket.objects.OrderItem

class DrinkMenu : AppCompatActivity() {


    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drink_menu)

        val totalCart = findViewById<TextView>(R.id.drinkMenuCartTotal)
        totalCart.setText(0.toString().plus(" €"))

        val club = intent.getSerializableExtra("club") as Club
        Log.d("TAG", club.name)

        val drinks = club.getClubDrinks(this)

        //var scrollView = findViewById<ScrollView>(R.id.drinkMenuScrollView)
        val layout = findViewById<LinearLayout>(R.id.drinkMenuLinearLayout)

        val fragmentManager = supportFragmentManager.fragments
        val transaction = supportFragmentManager.beginTransaction()

        for (fragment in fragmentManager) {
            transaction.remove(fragment)
        }

        var i = 1
        drinks.forEach { e ->
            val frame = FrameLayout(this)
            frame.id = i
            layout.addView(frame)
            transaction.add(i, DrinkElement.newInstance(e), e.name)
            i++
        }
        transaction.commit()

        val checkoutButton = findViewById<Button>(R.id.drinkMenuCheckoutButon)
        checkoutButton.setOnClickListener {
            val order = Order()
            order.club = club
            drinks.forEach { e ->
                val drinkElement = supportFragmentManager.findFragmentByTag(e.name) as DrinkElement
                if (drinkElement.getQuantity() > 0) {
                    val orderItem = OrderItem(
                        0,
                        drinkElement.getName(),
                        drinkElement.getQuantity(),
                        drinkElement.getPrice(),
                        ItemType.Drink,
                        0
                    )
                    order.drinks.add(orderItem)
                }
            }

            val intent = Intent(applicationContext, Payment::class.java)
            intent.putExtra("OrderPreview", order)
            startActivity(intent)

        }
    }
}