package com.hackerini.discoticket.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.ReviewElement
import com.hackerini.discoticket.objects.User
import com.hackerini.discoticket.objects.UserWithReviews
import com.hackerini.discoticket.room.RoomManager

class MyReviews : Fragment() {
    lateinit var warningText: TextView
    lateinit var reviewsLinearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_reviews, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        warningText = view.findViewById(R.id.MyReviewEmptyWarning)
        reviewsLinearLayout = view.findViewById(R.id.MyReviewLinearLayout)
        loadContent()
    }

    fun getReviews(): List<UserWithReviews>? {
        val reviewDao = RoomManager(requireContext()).db.reviewDao()
        val user = User.getLoggedUser(requireContext())
        if (user == null) return null
        return reviewDao.getAllReviewsOfUser(user.id)
    }

    fun loadContent() {
        checkWarningText()
        if (User.isLogged(requireContext())) {
            val reviews = getReviews()
            val dbUser = reviews!!.first().user
            reviews.first().reviews.forEach { e -> e.user = dbUser }

            if (reviews.firstOrNull()?.reviews?.isNotEmpty() == true) {
                val transaction = parentFragmentManager.beginTransaction()
                reviews.first().reviews.forEach { r ->
                    val fragment = ReviewElement.newInstance(r, true)
                    fragment.onRefreshNeeded = { checkWarningText() }
                    transaction.add(reviewsLinearLayout.id, fragment)
                }
                transaction.commit()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MyReviews()
    }

    var runOnResume = false
    override fun onResume() {
        super.onResume()
        if (runOnResume) {
            reviewsLinearLayout.removeAllViews()
            loadContent()
        }
        runOnResume = true
    }

    fun checkWarningText() {
        val reviews = getReviews()
        if (User.isLogged(requireContext()))
            warningText.text = "Non ci sono recensioni per l'utente corrente"
        else
            warningText.text = "Devi effettuare l'accesso per visualizzare le tue recensioni"

        if (reviews?.firstOrNull()?.reviews?.isEmpty() != false) {
            warningText.visibility = View.VISIBLE
        } else {
            warningText.visibility = View.GONE
        }
    }
}