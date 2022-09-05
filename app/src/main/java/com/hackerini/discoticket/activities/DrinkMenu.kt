package com.hackerini.discoticket.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.DrinkElement
import com.hackerini.discoticket.fragments.elements.EventElement
import com.hackerini.discoticket.objects.Drink

class DrinkMenu : AppCompatActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drink_menu)

        //var scrollView = findViewById<ScrollView>(R.id.drinkMenuScrollView)
        var layout= findViewById<LinearLayout>(R.id.drinkMenuLinearLayout)

        val manager = supportFragmentManager.fragments
        val transaction = supportFragmentManager.beginTransaction()

        for (fragment in manager) {
            transaction.remove(fragment)
        }

        val drink1= Drink("Negroni",10)
        val drink2= Drink("Mojito",20)

        val frame = FrameLayout(this)
        frame.id=1
        layout.addView(frame)
        transaction.add(1,DrinkElement.newInstance(drink1) )

        val frame2 = FrameLayout(this)
        frame2.id=2
        layout.addView(frame2)
        transaction.add(2,DrinkElement.newInstance(drink2) )
        transaction.commit()

        /*
        LinearLayout layout = (LinearLayout)findViewById(R.id.linear);
        FragmentTxn txn = getFragmentManager.beginTransaction();
        int i = 1; // This seems really fragile though
        for (Fragment f : fragments) {
              FrameLayout frame = new FrameLayout(this);
              frame.setId(i);
              layout.addView(frame);
              txn.add(i, f);
              i++;
         }
         txn.commit();
         */



        /*transaction.add(R.id.drinkMenuLinearLayout, DrinkElement.newInstance(drink1))
        transaction.add(R.id.drinkMenuLinearLayout, DrinkElement.newInstance(drink2))
        transaction.commitNow()*/
    }
}