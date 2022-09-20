package com.hackerini.discoticket.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Review
import com.hackerini.discoticket.objects.User
import com.hackerini.discoticket.room.RoomManager

class WriteReview : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_review)

        val club = intent.getSerializableExtra("club") as Club
        val reviewDao = RoomManager(this).db.reviewDao()

        val rating = findViewById<RatingBar>(R.id.writeReviewRatingBar)
        val description =findViewById<EditText>(R.id.writeReviewDescriptionText)
        description.setText("")
        val addReviewButton= findViewById<Button>(R.id.writeReviewAddReviewButton)

        addReviewButton.setOnClickListener {

            val context : Context= this
            if(User.isLogged(this)) {

                val review = Review(User.getLoggedUser(this)?.id!!)
                review.description = description.toString()

                if (rating.rating != 0f) {
                    review.rating = rating.rating.toDouble()
                    reviewDao.insert(review)
                    club.reviews?.plus(review)

                    val builder = MaterialAlertDialogBuilder(context)
                    builder.setTitle("Recensione Aggiunta")
                    builder.setMessage("Grazie per aver lasciato una recensione")
                    builder.create().show()
                }

                else{
                    val builder = MaterialAlertDialogBuilder(context)
                    builder.setTitle("Attenzione la recensione non contiene una valutazione")
                    builder.setMessage("Per lasciare una recensione devi aggiungere una valutazione")
                    builder.create().show()
                }




            }
            else{

                val builder = MaterialAlertDialogBuilder(context)
                builder.setTitle("Accesso non effettuato")
                builder.setMessage("Per proseguire è necessario effettuare l'accesso")
                builder.setPositiveButton("Accedi") { _, _ ->
                    context.startActivity(Intent(context, Login::class.java))
                }
                builder.setNegativeButton("Annulla") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.create().show()
            }


        }

    }
}