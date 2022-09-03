package com.hackerini.discoticket.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.Club
import com.squareup.picasso.Picasso

class ClubDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_details)

        val club = intent.getSerializableExtra("club") as Club
        Log.d("TAG", club.name)

        val clubImage = findViewById<ImageView>(R.id.clubDetailClubImage)
        val clubName = findViewById<TextView>(R.id.clubDetailsClubName)
        val address= findViewById<TextView>(R.id.clubDetailsAddress)
        val ratingBar = findViewById<RatingBar>(R.id.clubDeatilsRatingBar)
        val totalReview = findViewById<TextView>(R.id.clubDetailsTotalReviews)
        val clubDescription= findViewById<TextView>(R.id.clubDeatilsClubDescription)


        clubName.setText(club.name)
        address.setText(club.address)
        ratingBar.rating = club.rating
        totalReview.text=club.reviewAmount.toString()+" "+"Recensioni"
        clubDescription.setText(club.description)


        val imageSize = 250
        Picasso.get().load(club.imgUrl).resize(imageSize, imageSize).into(clubImage)

        val drinkMenuButton = findViewById<Button>(R.id.clubDetailsDrinkMenuButton) as Button
        drinkMenuButton.setOnClickListener {
            val intent = Intent(this, DrinkMenu::class.java)
            startActivity(intent)
        }

        val buyTicketsButton = findViewById<Button>(R.id.clubDetailsBuyTicketsButton) as Button
        buyTicketsButton.setOnClickListener {

            //val intent = Intent(this, BuyTickets::class.java)
            //startActivity(intent)
        }










        /*
        Inside the object club you have the attributes needed to build the UI

        club.name
        club.rating
        club.address
         */


    }

}