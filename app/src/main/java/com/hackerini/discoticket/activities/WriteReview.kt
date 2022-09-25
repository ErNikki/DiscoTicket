package com.hackerini.discoticket.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.ReviewElement
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Review
import com.hackerini.discoticket.objects.User
import com.hackerini.discoticket.room.RoomManager
import java.text.SimpleDateFormat
import java.util.*

class WriteReview : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_review)

        val club = intent.getSerializableExtra("club") as Club
        val originalReview = intent.getSerializableExtra("review") as Review?

        val reviewDao = RoomManager(this).db.reviewDao()
        val userDao = RoomManager(this).db.userDao()

        val rating = findViewById<RatingBar>(R.id.writeReviewRatingBar)
        val description = findViewById<EditText>(R.id.writeReviewDescriptionText)
        val addReviewButton = findViewById<Button>(R.id.writeReviewAddReviewButton)

        val explanation = findViewById<TextView>(R.id.writeReviewExplanation)
        explanation.text = "Pubblicando una recensione otterrai " + resources.getInteger(R.integer.pointPerReview) + " punti"

        if (originalReview != null) {
            rating.rating = originalReview.rating.toFloat()
            description.setText(originalReview.description)
            addReviewButton.text = "Modifica"
        }

        addReviewButton.setOnClickListener {

            val context: Context = this
            if (User.isLogged(this)) {

                val review = Review(User.getLoggedUser(this)?.id!!)
                review.description = description.text.toString()
                review.date = SimpleDateFormat("dd/MM/yyy", Locale.getDefault()).format(Date())
                review.clubId = club.id

                if (rating.rating != 0f) {
                    review.rating = rating.rating.toDouble()
                    val builder = MaterialAlertDialogBuilder(context)
                    if (originalReview != null) {
                        //Edit
                        review.reviewId = originalReview.reviewId
                        builder.setTitle("Recensione modificata")
                        builder.setMessage("La tua recensione è stata aggiornata con successo")
                        reviewDao.editReview(review)
                        builder.setPositiveButton("Ok") { dialog, _ -> finish() }
                    } else {
                        //New review
                        builder.setTitle("Conferma pubblicazione")
                        builder.setMessage("Sei sicuro di voler pubblicare questa recensione?")

                        /*val frameLayout = FrameLayout(this)
                        frameLayout.id = View.generateViewId()
                        builder.setView(frameLayout)

                        val myReview = club.getReview(this).toList().sortedByDescending { review -> review.getLongTime() }
                        val transaction = supportFragmentManager.beginTransaction()
                        val fragmentElement = ReviewElement.newInstance(myReview.first())
                        //fragmentElement.onRefreshNeeded = { loadContent() }
                        transaction.add(frameLayout.id, fragmentElement)
                        transaction.commit()*/

                        builder.setNegativeButton("No") { dialog, _ -> null }
                        builder.setPositiveButton("Sì") { dialog, _ ->
                            reviewDao.insert(review)
                            userDao.incrementsPoints(
                                resources.getInteger(R.integer.pointPerReview),
                                User.getLoggedUser(this)!!.id
                            )
                            dialog.dismiss()

                            var builder2 = MaterialAlertDialogBuilder(context)
                            builder2.setTitle("Recensione aggiunta")
                            builder2.setMessage(
                                "Grazie per aver lasciato una recensione!\nHai ottenuto ${
                                    resources.getInteger(R.integer.pointPerReview)
                                } punti che potrai usare per ottenere buoni sconto"
                            )
                            builder2.setPositiveButton("Ok") { dialog, _ -> finish() }
                            builder2.create().show()
                        }
                    }
                    builder.create().show()

                } else {
                    val builder = MaterialAlertDialogBuilder(context)
                    builder.setTitle("Attenzione: la recensione non contiene una valutazione")
                    builder.setMessage("Per lasciare una recensione devi aggiungere una valutazione")
                    builder.setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                    builder.create().show()
                }
            } else {

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