package com.hackerini.discoticket.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Review
import com.hackerini.discoticket.room.RoomManager

class WriteReview : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_review)

        val club = intent.getSerializableExtra("club") as Club
        val reviewDao = RoomManager(this).db.reviewDao()

        val rating = findViewById<RatingBar>(R.id.writeReviewRatingBar)
        val description =findViewById<EditText>(R.id.writeReviewDescriptionText)
        val addReviewButton= findViewById<Button>(R.id.writeReviewAddReviewButton)
        val fakeId=200
        addReviewButton.setOnClickListener {
            val review= Review( fakeId, rating.rating, description.text.toString())
            reviewDao.insert(review)
        }

        val bottone =findViewById<Button>(R.id.cazzo)
        bottone.setOnClickListener {
            //Log.d("prova", reviewDao.getAllReviewsOfUser(fakeId).reviews.toString())
        }

    }
}