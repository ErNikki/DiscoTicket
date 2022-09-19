package com.hackerini.discoticket.activities

import android.media.Rating
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Review
import com.hackerini.discoticket.objects.ReviewDao
import com.hackerini.discoticket.room.RoomManager

class WriteReview : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_review)

        val club = intent.getSerializableExtra("club") as Club
        var reviewDao = RoomManager(this).db.reviewDao()

        var rating = findViewById<RatingBar>(R.id.writeReviewRatingBar)
        var description =findViewById<EditText>(R.id.writeReviewDescriptionText)
        var addReviewButton= findViewById<Button>(R.id.writeReviewAddReviewButton)
        var fakeId=200
        addReviewButton.setOnClickListener {
            val review= Review( fakeId, rating.rating, description.text.toString())
            reviewDao.insert(review)
        }

        var bottone =findViewById<Button>(R.id.cazzo)
        bottone.setOnClickListener {
            Log.d("prova", reviewDao.getAllReviewsOfUser(fakeId).reviews.toString())
        }

    }
}