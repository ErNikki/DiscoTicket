package com.hackerini.discoticket.activities

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.ReviewElement
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Review
import com.squareup.picasso.Picasso
import com.taufiqrahman.reviewratings.BarLabels
import com.taufiqrahman.reviewratings.RatingReviews


class AllReview : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_review)

        val club = intent.getSerializableExtra("club") as Club
        val reviews = club.reviews

        val imageSize = 250
        Picasso.get().load(club.imgUrl).resize(imageSize, imageSize)
            .into(findViewById<ImageView>(R.id.AllReviewImage))
        findViewById<TextView>(R.id.AllReviewName).text = club.name

        findViewById<TextView>(R.id.AllReviewReviewAmount).text = "(${reviews.size} recensioni)"
        val r : Float
        val reviewAvg = reviews.sumOf { r :Review -> r.rating  } / reviews.size.toFloat()
        findViewById<TextView>(R.id.AllReviewAvg).text = String.format("%.1f", reviewAvg)
        findViewById<RatingBar>(R.id.AllReviewRating).rating = reviewAvg.toFloat()


        val ratingReviews = findViewById<View>(R.id.rating_reviews) as RatingReviews

        val colors = intArrayOf(
            Color.parseColor("#0e9d58"),
            Color.parseColor("#bfd047"),
            Color.parseColor("#ffc105"),
            Color.parseColor("#ef7e14"),
            Color.parseColor("#d36259")
        )

        val raters = intArrayOf(
            reviews.filter { r :Review -> r.rating == 5.0 }.size,
            reviews.filter { r :Review-> r.rating == 4.0 }.size,
            reviews.filter { r :Review-> r.rating == 3.0 }.size,
            reviews.filter { r :Review-> r.rating == 2.0 }.size,
            reviews.filter { r :Review-> r.rating == 1.0 }.size,
        )

        ratingReviews.createRatingBars(1, BarLabels.STYPE1, colors, raters)

        val transaction = supportFragmentManager.beginTransaction()
        reviews.forEach { review :Review ->
            transaction.add(R.id.AllReviewLinearLayout, ReviewElement.newInstance(review))
        }
        transaction.commit()
    }
}