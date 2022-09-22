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
    WorstRating,
    BestRating
}

class AllReview : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var reviews: List<Review>? = null
    var orderCriteria = ReviewOrderCriteria.MostRecent
    lateinit var club: Club
    private val IMAGE_SIZE = 250

    private lateinit var ratingReviews: RatingReviews
    private lateinit var clubImage: ImageView
    private lateinit var clubName: TextView
    private lateinit var allReviewAmount: TextView
    private lateinit var allReviewAvg: TextView
    private lateinit var locationSpinner: Spinner
    private lateinit var allReviewRating: RatingBar
    private lateinit var linearLayout: LinearLayout

    private val colors = intArrayOf(
        Color.parseColor("#0e9d58"),
        Color.parseColor("#bfd047"),
        Color.parseColor("#ffc105"),
        Color.parseColor("#ef7e14"),
        Color.parseColor("#d36259")
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_review)

        club = intent.getSerializableExtra("club") as Club
        reviews = club.getReview(this).toList()

        ratingReviews = findViewById(R.id.rating_reviews)
        clubImage = findViewById(R.id.AllReviewImage)
        clubName = findViewById(R.id.AllReviewName)
        locationSpinner = findViewById(R.id.AllReviewOrderSpinner)
        allReviewAmount = findViewById(R.id.AllReviewReviewAmount)
        allReviewAvg = findViewById(R.id.AllReviewAvg)
        allReviewRating = findViewById(R.id.AllReviewRating)
        linearLayout = findViewById(R.id.AllReviewLinearLayout)

        Picasso.get().load(club.imgUrl).resize(IMAGE_SIZE, IMAGE_SIZE).into(clubImage)
        clubName.text = club.name

        val languages = resources.getStringArray(R.array.AllReviewOrderBy)
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languages)
        locationSpinner.adapter = adapter
        locationSpinner.onItemSelectedListener = this

        loadContent()
    }

    private fun loadContent() {
        reviews = club.getReview(this).toList()

        //Update amount and avg
        allReviewAmount.text = "(${reviews?.size} recensioni)"
        val reviewAvg = reviews!!.sumOf { r -> r.rating } / reviews!!.size.toFloat()
        allReviewAvg.text = String.format("%.1f", reviewAvg)
        allReviewRating.rating = reviewAvg.toFloat()

        //Update chart
        val raters = intArrayOf(
            reviews!!.filter { r -> r.rating.toInt() == 5 }.size,
            reviews!!.filter { r -> r.rating.toInt() == 4 }.size,
            reviews!!.filter { r -> r.rating.toInt() == 3 }.size,
            reviews!!.filter { r -> r.rating.toInt() == 2 }.size,
            reviews!!.filter { r -> r.rating.toInt() == 1 }.size,
        )
        ratingReviews.createRatingBars(1, BarLabels.STYPE1, colors, raters)

        //Update list
        val orderedReviews = when (orderCriteria) {
            ReviewOrderCriteria.MostRecent -> reviews?.sortedByDescending { review -> review.getLongTime() }
            ReviewOrderCriteria.LastRecent -> reviews?.sortedBy { review -> review.getLongTime() }
            ReviewOrderCriteria.BestRating -> reviews?.sortedBy { review -> review.rating }
            ReviewOrderCriteria.WorstRating -> reviews?.sortedByDescending { review -> review.rating }
        }

        val transaction = supportFragmentManager.beginTransaction()
        orderedReviews?.forEach { review ->
            val fragmentElement = ReviewElement.newInstance(review)
            fragmentElement.onRefreshNeeded = { loadContent() }
            transaction.add(R.id.AllReviewLinearLayout, fragmentElement)
        }
        transaction.commit()
    }

    private var canCallLoadContent = false
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (canCallLoadContent) {
            orderCriteria = ReviewOrderCriteria.values()[position]
            linearLayout.removeAllViews()
            loadContent()
        }
        canCallLoadContent = true
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    var runOnResume = false
    override fun onResume() {
        super.onResume()
        if (runOnResume) {
            linearLayout.removeAllViews()
            loadContent()
        }
        runOnResume = true
    }
}