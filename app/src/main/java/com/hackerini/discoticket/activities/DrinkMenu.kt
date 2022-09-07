package com.hackerini.discoticket.activities

import android.annotation.SuppressLint
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
import com.hackerini.discoticket.objects.Drink

class DrinkMenu : AppCompatActivity() {



    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drink_menu)

        val totalCart=findViewById<TextView>(R.id.drinkMenuCartTotal)
        totalCart.setText(0.toString().plus(" €"))

        val club = intent.getSerializableExtra("club") as Club
        Log.d("TAG", club.name)

        val drinks = club.drinks

        //var scrollView = findViewById<ScrollView>(R.id.drinkMenuScrollView)
        val layout = findViewById<LinearLayout>(R.id.drinkMenuLinearLayout)

        val fragmentManager = supportFragmentManager.fragments
        val transaction = supportFragmentManager.beginTransaction()

        for (fragment in fragmentManager) {
            transaction.remove(fragment)
        }

        var i = 0
        drinks.forEach { e ->
            val drink = Drink(e)
            val frame = FrameLayout(this)
            frame.id = i
            layout.addView(frame)
            transaction.add(i, DrinkElement.newInstance(drink), e)
            i++
        }
        transaction.commit()

        val checkoutButton = findViewById<Button>(R.id.drinkMenuCheckoutButon)
        checkoutButton.setOnClickListener {
            //manca solo buildare l'order item!!!!!!
            drinks.forEach { e ->
                var drinkElement = getSupportFragmentManager().findFragmentByTag(e) as DrinkElement
                Log.d("tag", drinkElement.getName())
                Log.d("tag", drinkElement.getPrice().toString())
                Log.d("tag", drinkElement.getQuantity().toString())
            }


            //Log.d("tag", )
        }
    }
}