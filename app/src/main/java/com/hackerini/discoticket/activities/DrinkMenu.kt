package com.hackerini.discoticket.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.DrinkElement
import com.hackerini.discoticket.fragments.views.InfoDialog
import com.hackerini.discoticket.objects.*

class DrinkMenu : AppCompatActivity() {
    private lateinit var fragments: List<DrinkElement>
    private lateinit var totalCart: TextView
    private lateinit var checkoutButton: Button

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drink_menu)

        checkoutButton = findViewById<Button>(R.id.drinkMenuCheckoutButon)
        totalCart = findViewById(R.id.drinkMenuCartTotal)
        totalCart.text = 0.toString().plus(" €")

        val club = intent.getSerializableExtra("club") as Club
        val drinks = club.getClubDrinks(this)
        val layout = findViewById<LinearLayout>(R.id.drinkMenuLinearLayout)
        val transaction = supportFragmentManager.beginTransaction()
        layout.removeAllViews()

        fragments = drinks.map { e -> DrinkElement.newInstance(e) }
        fragments.forEach { e -> e.onQuantityChange = { onQuantityChange() } }

        fragments.forEach { e ->
            transaction.add(layout.id, e)
        }
        transaction.commit()

        val dialog = InfoDialog(
            "Come acquistare",
            "Scegli in questa pagina quanti drink vuoi ordinare, dopodiché effettua il pagamento direttamente nell'app: verrà generato un codice QR da mostrare al barista, così ti darà i drink che hai ordinato!",
            this
        )
        if (dialog.shouldBeOpened())
            dialog.show()



        checkoutButton.setOnClickListener {
            if (User.isLogged(this)) {
                val order = Order()
                order.club = club
                fragments.forEach { e ->
                    if (e.quantity > 0) {
                        val orderItem = OrderItem(
                            0,
                            e.drink!!.name,
                            e.quantity,
                            e.drink!!.price,
                            ItemType.Drink,
                            0
                        )
                        order.drinks.add(orderItem)
                    }
                }

                val intent = Intent(applicationContext, Payment::class.java)
                intent.putExtra("OrderPreview", order)
                startActivity(intent)
            } else {
                User.generateNotLoggedAlertDialog(this).show()
            }
        }
    }

    private fun onQuantityChange() {
        val sum = fragments.map { e -> e.quantity.toFloat() * e.drink!!.price }.sum()

        checkoutButton.isEnabled = sum > 0
        totalCart.text = String.format("%.2f", sum).plus("€")
    }
}