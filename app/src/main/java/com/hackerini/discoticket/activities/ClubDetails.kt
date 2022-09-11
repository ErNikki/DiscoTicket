package com.hackerini.discoticket.activities

import android.app.ActionBar
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.fragment.app.FragmentContainerView
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.ReviewElement
import com.hackerini.discoticket.objects.Club
import com.squareup.picasso.Picasso

class ClubDetails : AppCompatActivity() {

    var club: Club? = null
    var favouritesButton: ImageButton? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_details)
        favouritesButton = findViewById(R.id.clubDetailsFavouritesButton)

        club = intent.getSerializableExtra("club") as Club
        club?.addToLastSeen(this)

        val clubImage = findViewById<ImageView>(R.id.clubDetailClubImage)
        val clubName = findViewById<TextView>(R.id.clubDetailsClubName)
        val address = findViewById<TextView>(R.id.clubDetailsAddress)
        val ratingBar = findViewById<RatingBar>(R.id.clubDeatilsRatingBar)
        val reviewsAvg = findViewById<TextView>(R.id.clubDetailsAvg)
        val totalReview = findViewById<TextView>(R.id.clubDetailsTotalReviews)
        val clubDescription = findViewById<TextView>(R.id.clubDeatilsClubDescription)
        val tagLayout = findViewById<LinearLayout>(R.id.clubDetailsTagLayout)
        val price = findViewById<TextView>(R.id.clubDetailsPrice)
        val distance = findViewById<TextView>(R.id.clubDetailsDistance)

        val reviews = club!!.reviews
        val average = reviews.sumOf { r -> r.rating } / reviews.size.toFloat()

        clubName.text = club!!.name
        address.text = club!!.address
        ratingBar.rating = average
        reviewsAvg.text = String.format("%.1f", average)
        totalReview.text = "(${reviews.size} recensioni)"
        clubDescription.text = club!!.description
        price.text = "Prezzi a partire da " + String.format("%.2f", club!!.simpleTicketPrice) + "€"
        distance.text = "Si trova a " + club!!.distanceFromYou + "km da te"

        val imageSize = 250
        Picasso.get().load(club!!.imgUrl).resize(imageSize, imageSize).into(clubImage)

        val drinkMenuButton = findViewById<Button>(R.id.clubDetailsDrinkMenuButton) as Button
        drinkMenuButton.setOnClickListener {
            val intent = Intent(this, DrinkMenu::class.java)
            intent.putExtra("club", club)
            startActivity(intent)
        }

        val buyTicketsButton = findViewById<Button>(R.id.clubDetailsBuyTicketsButton) as Button
        buyTicketsButton.setOnClickListener {
            val intent = Intent(this, BuyTicket::class.java)
            intent.putExtra("club", club)
            startActivity(intent)
        }

        val params =
            LinearLayout.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            )
        params.setMargins(0, 0, 10, 0)

        club!!.labels.forEach { e ->
            val shape = GradientDrawable()
            shape.cornerRadius = 10f
            shape.setColor(Club.getLabelColorFromName(e))

            val textview = TextView(this)
            textview.setText(e)
            textview.background = shape
            textview.setPadding(8)
            textview.layoutParams = params
            tagLayout.addView(textview)
        }

        //Review
        if (club!!.reviews.isEmpty()) {
            findViewById<FragmentContainerView>(R.id.clubDetailsFragmentContainerView).visibility =
                View.GONE
            findViewById<TextView>(R.id.clubDeatilsReviwerNoReview).visibility =
                View.VISIBLE
        } else {
            supportFragmentManager.beginTransaction().add(
                R.id.clubDetailsFragmentContainerView, ReviewElement.newInstance(
                    club!!.reviews.first()
                )
            ).commit()
        }
        findViewById<Button>(R.id.clubDetailsReadReviewsButton).setOnClickListener {
            val intent = Intent(applicationContext, AllReview::class.java)
            intent.putExtra("club", club)
            startActivity(intent)
        }

        findViewById<Button>(R.id.clubDetailsWriteReviewButton).setOnClickListener {
            val intent = Intent(applicationContext, WriteReview::class.java)
            intent.putExtra("club", club)
            startActivity(intent)
        }

        updateButtonStatus()
        favouritesButton?.setOnClickListener {
            if (club?.isFavorite(this) == true) {
                club?.removeToFavorite(this)
            } else
                club?.addToFavorite(this)
            updateButtonStatus()
        }
    }

    fun updateButtonStatus() {
        val favouriteText = findViewById<TextView>(R.id.clubDeatilsAddToFavouritiesText)

        if (club?.isFavorite(this) == true) {
            favouritesButton?.setImageResource(R.drawable.ic_baseline_favorite_on_24)
            favouriteText.text = "Rimuovi dai preferiti"
        } else {
            favouritesButton?.setImageResource(R.drawable.ic_baseline_favorite_off_24)
            favouriteText.text = "Aggiungi ai preferiti"
        }
    }

}