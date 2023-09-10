package com.hackerini.discoticket.activities

import android.app.ActionBar
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.ElementToShow
import com.hackerini.discoticket.fragments.elements.HomePageEventElement
import com.hackerini.discoticket.fragments.elements.ReviewElement
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.User
import com.hackerini.discoticket.room.RoomManager
import com.hackerini.discoticket.utils.EventsManager
import com.hackerini.discoticket.utils.ObjectLoader
import com.squareup.picasso.Picasso

class ClubDetails : AppCompatActivity() {

    var club: Club? = null
    var favouritesButton: ImageButton? = null
    lateinit var fragmentContainerView: FragmentContainerView
    private lateinit var writeReviewButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_details)
        favouritesButton = findViewById(R.id.clubDetailsFavouritesButton)

        club = intent.getSerializableExtra("club") as Club
        Club.addToLastSeen(this, club!!.id, Club::class)

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
        val showEventsButton = findViewById<ImageButton>(R.id.ClubDetailsShowEvents)
        val eventsLayout = findViewById<LinearLayout>(R.id.ClubDetailsEventsLayout)

        fragmentContainerView =
            findViewById(R.id.clubDetailsFragmentContainerView)
        writeReviewButton = findViewById(R.id.clubDetailsWriteReviewButton)


        val reviews = club!!.reviews
        val average = reviews.sumOf { r -> r.rating } / reviews.size.toFloat()
        val boldSpannableString = SpannableString(
            "Ingresso semplice a " + String.format(
                "%.2f",
                club!!.simpleTicketPrice
            ) + "€"
        )
        boldSpannableString.setSpan(StyleSpan(Typeface.BOLD), 19, boldSpannableString.length, 0)

        clubName.text = club!!.name
        address.text = club!!.address
        ratingBar.rating = average.toFloat()
        reviewsAvg.text = String.format("%.1f", average)
        totalReview.text = "(${reviews.size} recensioni)"
        clubDescription.text = club!!.description
        price.text = boldSpannableString
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

        findViewById<Button>(R.id.clubDetailsReadReviewsButton).setOnClickListener {
            val intent = Intent(applicationContext, AllReview::class.java)
            intent.putExtra("club", club)
            startActivity(intent)
        }

        writeReviewButton.setOnClickListener {
            if (userHasAReviewForThisClub()) {
                val dialog = MaterialAlertDialogBuilder(this)
                dialog.setTitle("Recensione già esistente")
                dialog.setMessage(
                    "Hai già scritto una recensione per questa discoteca.\n" +
                            "Vai nella schermata \"Le mie recensioni\" per modificarla"
                )
                dialog.setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                dialog.create().show()
            } else if (User.isLogged(this)) {
                val intent = Intent(applicationContext, WriteReview::class.java)
                intent.putExtra("club", club)
                startActivity(intent)
            } else {
                User.generateNotLoggedAlertDialog(this).show()
            }
        }

        updateButtonStatus()
        favouritesButton?.setOnClickListener {
            if (club?.isFavorite(this) == true) {
                club?.removeToFavorite(this)
            } else
                club?.addToFavorite(this)
            updateButtonStatus()
        }

        showEventsButton.setOnClickListener {
            if (eventsLayout.visibility == View.VISIBLE) {
                showEventsButton.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
                eventsLayout.visibility = View.GONE
            } else {
                showEventsButton.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
                eventsLayout.visibility = View.VISIBLE
            }
        }

        val events = EventsManager.getEvents()
            .filter { event -> event.clubId == this.club?.id }
            .sortedBy { event -> event.date }
        if (events.isEmpty()) {
            val text = TextView(this)
            text.text = "Non ci sono eventi in programma per questa discoteca"
            text.setPadding(0, 0, 0, 8)
            eventsLayout.addView(text)
        } else {
            val transaction = supportFragmentManager.beginTransaction()
            for (event in events) {
                transaction.add(
                    eventsLayout.id,
                    HomePageEventElement.newInstance(event, ElementToShow.Date)
                )
            }
            transaction.commit()
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

    override fun onResume() {
        super.onResume()

        fragmentContainerView.removeAllViews()
        //Review
        if (club!!.reviews.isEmpty()) {
            fragmentContainerView.visibility =
                View.GONE
            findViewById<TextView>(R.id.clubDeatilsReviwerNoReview).visibility =
                View.VISIBLE
        } else {
            loadFirstReview()
        }
    }

    fun loadFirstReview() {
        val flag=User.isLogged(this)
        val review=club!!.getReview(this)
            .filter { r -> r.description.isNotBlank() }
            .sortedByDescending { r -> r.getLongTime() }
            .first()
        val fragment = ReviewElement.newInstance(review, false, flag)
        fragment.onRefreshNeeded = { loadFirstReview() }
        supportFragmentManager.beginTransaction().add(fragmentContainerView.id, fragment).commit()
    }

    private fun userHasAReviewForThisClub(): Boolean {
        /*
        val reviewDao = RoomManager(this).db.reviewDao()
        val user = User.getLoggedUser(this)
        return if (user != null)
            reviewDao.userHasReviewForThisClub(user.id, club!!.id)
        else false

         */
        return false
    }

}