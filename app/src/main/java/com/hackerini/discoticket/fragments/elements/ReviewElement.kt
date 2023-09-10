package com.hackerini.discoticket.fragments.elements

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import com.avatarfirst.avatargenlib.AvatarGenerator
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.reflect.TypeToken
import com.hackerini.discoticket.R
import com.hackerini.discoticket.activities.ClubDetails
import com.hackerini.discoticket.activities.WriteReview
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Review
import com.hackerini.discoticket.objects.User
import com.hackerini.discoticket.room.RoomManager
import com.hackerini.discoticket.utils.ObjectLoader
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.HttpResponse
import io.ktor.http.parameters
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.lang.Math.abs
import java.security.MessageDigest
import com.google.gson.Gson
import com.hackerini.discoticket.utils.ClubsManager
import com.hackerini.discoticket.utils.ReviewsManager

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

class ReviewElement : Fragment() {
    private var review: Review? = null
    private var showClubName = false
    private var isUserLogged = false
    private var club: Club? = null
    var onRefreshNeeded: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            review = it.getSerializable(ARG_PARAM1) as Review
            showClubName = it.getBoolean(ARG_PARAM2)
            isUserLogged = it.getBoolean(ARG_PARAM3)
        }

        club =
            ClubsManager.getClubs().firstOrNull { club ->
                club.id == review?.clubId
            }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review_element, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reviewerImage = view.findViewById<ImageView>(R.id.ReviewReviewerImage)
        val reviewerName = view.findViewById<TextView>(R.id.ReviewReviewerName)
        val reviewDate = view.findViewById<TextView>(R.id.ReviewReviewDate)
        val reviewContent = view.findViewById<TextView>(R.id.ReviewReviewText)
        val reviewRatingBar = view.findViewById<RatingBar>(R.id.clubDeatilsReviwerRatingBar)
        val deleteButton = view.findViewById<ImageButton>(R.id.ReviewDeleteButton)
        val editButton = view.findViewById<ImageButton>(R.id.ReviewEditButton)
        val clubName = view.findViewById<TextView>(R.id.ReviewClubName)

        val name = review!!.user.name
        val surname = review!!.user.surname


        if (showClubName && club != null) {
            clubName.text = club!!.name
            clubName.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            clubName.setOnClickListener {
                val intent = Intent(requireContext(), ClubDetails::class.java)
                intent.putExtra("club", club)
                startActivity(intent)
            }
        } else
            clubName.visibility = View.GONE

        reviewerName.text = name + " " + surname
        reviewDate.text = review?.date
        reviewContent.text = review?.description
        reviewRatingBar.rating = review?.rating!!.toFloat()
        //Picasso.get().load(review?.user?.imageProfileUrl).resize(100, 100).into(reviewerImage)
        if (reviewContent.text == "")
            reviewContent.visibility = View.GONE

        val image = AvatarGenerator.AvatarBuilder(requireContext())
            .setLabel(name)
            .setAvatarSize(100)
            .setTextSize(30)
            .toSquare()
            .toCircle()
            .setBackgroundColor(getRandomColor(review!!.user))
            .build()
        reviewerImage.setImageDrawable(image)

        if (isUserLogged && review?.user?.id == User.getLoggedUser(requireContext())?.id) {
            editButton.visibility = View.VISIBLE
            deleteButton.visibility = View.VISIBLE
            editButton.setOnClickListener { onEditClick() }
            deleteButton.setOnClickListener { onDeleteClick() }
        }
    }

    fun getRandomColor(user: User): Int {
        val list = arrayOf(
            0xfff44336, 0xffe91e63, 0xff9c27b0, 0xff673ab7,
            0xff3f51b5, 0xff2196f3, 0xff03a9f4, 0xff00bcd4,
            0xff009688, 0xff4caf50, 0xff8bc34a, 0xffcddc39,
            0xffffeb3b, 0xffffc107, 0xffff9800, 0xffff5722,
            0xff795548, 0xff9e9e9e, 0xff607d8b, 0xff333333
        )
        val md = MessageDigest.getInstance("MD5")
        val number = md.digest((user.name + user.surname).toByteArray()).sum()
        return list[abs(number) % list.size].toInt()
    }

    fun onDeleteClick() {
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
        dialogBuilder.setTitle("Conferma eliminazione")
        dialogBuilder.setMessage("Sei sicuro di voler eliminare la recensione?")
        dialogBuilder.setNegativeButton("Annulla") { dialog, _ -> dialog.dismiss() }
        dialogBuilder.setPositiveButton("Conferma") { dialog, _ ->
            dialog.dismiss()
            hideAndDelete()
        }
        dialogBuilder.create().show()
    }

    fun onEditClick() {
        val intent = Intent(requireContext(), WriteReview::class.java)
        intent.putExtra("club", club)
        intent.putExtra("review", review!!)
        startActivity(intent)
    }

    private fun hideAndDelete() {

        val valueAnimator = ValueAnimator.ofInt(requireView().height, 0)
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.addUpdateListener { animation ->
            requireView().layoutParams.height = animation.animatedValue as Int
            requireView().requestLayout()
        }
        valueAnimator.duration = 300
        valueAnimator.doOnEnd {
            ReviewsManager.delete(review!!)
            onRefreshNeeded?.invoke()
        }
        valueAnimator.start()
    }

    companion object {
        @JvmStatic
        fun newInstance(review: Review, showClubName: Boolean = false, isUserLogged: Boolean =  false) =
            ReviewElement().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, review)
                    putBoolean(ARG_PARAM2, showClubName)
                    putBoolean(ARG_PARAM3, isUserLogged)
                }
            }
    }
}