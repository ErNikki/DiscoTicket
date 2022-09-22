package com.hackerini.discoticket.activities

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.ReviewElement
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Review
import com.squareup.picasso.Picasso
import com.taufiqrahman.reviewratings.BarLabels
import com.taufiqrahman.reviewratings.RatingReviews

enum class ReviewOrderCriteria {
    MostRecent,
    LastRecent,
    BestRating,
    WorstRating
}

class AllReview : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var reviews: List<Review>? = null
    var orderCriteria = ReviewOrderCriteria.MostRecent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_review)

        val club = intent.getSerializableExtra("club") as Club
        reviews = club.getReview(this).toList()

        val imageSize = 250
        Picasso.get().load(club.imgUrl).resize(imageSize, imageSize)
            .into(findViewById<ImageView>(R.id.AllReviewImage))
        findViewById<TextView>(R.id.AllReviewName).text = club.name

        findViewById<TextView>(R.id.AllReviewReviewAmount).text = "(${reviews?.size} recensioni)"
        val reviewAvg = reviews!!.sumOf { r -> r.rating } / reviews!!.size.toFloat()
        findViewById<TextView>(R.id.AllReviewAvg).text = String.format("%.1f", reviewAvg)
        findViewById<RatingBar>(R.id.AllReviewRating).rating = reviewAvg.toFloat()

        val locationSpinner = findViewById<Spinner>(R.id.AllReviewOrderSpinner)
        val languages = resources.getStringArray(R.array.AllReviewOrderBy)
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languages)
        locationSpinner.adapter = adapter
        locationSpinner.onItemSelectedListener = this


        val ratingReviews = findViewById<View>(R.id.rating_reviews) as RatingReviews

        val colors = intArrayOf(
            Color.parseColor("#0e9d58"),
            Color.parseColor("#bfd047"),
            Color.parseColor("#ffc105"),
            Color.parseColor("#ef7e14"),
            Color.parseColor("#d36259")
        )

        val raters = intArrayOf(
            reviews!!.filter { r -> r.rating.toInt() == 5 }.size,
            reviews!!.filter { r -> r.rating.toInt() == 4 }.size,
            reviews!!.filter { r -> r.rating.toInt() == 3 }.size,
            reviews!!.filter { r -> r.rating.toInt() == 2 }.size,
            reviews!!.filter { r -> r.rating.toInt() == 1 }.size,
        )

        ratingReviews.createRatingBars(1, BarLabels.STYPE1, colors, raters)

        loadContent()
    }

    private fun loadContent() {
        val orderedReviews = when (orderCriteria) {
            ReviewOrderCriteria.MostRecent -> reviews?.sortedByDescending { review -> review.getLongTime() }
            ReviewOrderCriteria.LastRecent -> reviews?.sortedBy { review -> review.getLongTime() }
            ReviewOrderCriteria.BestRating -> reviews?.sortedBy { review -> review.rating }
            ReviewOrderCriteria.WorstRating -> reviews?.sortedByDescending { review -> review.rating }
        }

        findViewById<LinearLayout>(R.id.AllReviewLinearLayout).removeAllViews()
        val transaction = supportFragmentManager.beginTransaction()
        orderedReviews?.forEach { review ->
            transaction.add(R.id.AllReviewLinearLayout, ReviewElement.newInstance(review))
        }
        transaction.commit()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        orderCriteria = ReviewOrderCriteria.values()[position]
        loadContent()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}