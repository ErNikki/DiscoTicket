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

    fun loadContent() {
        val reviewDao = RoomManager(requireContext()).db.reviewDao()
        if (User.isLogged(requireContext())) {
            val user = User.getLoggedUser(requireContext())
            val reviews = reviewDao.getAllReviewsOfUser(user!!.id)
            val dbUser = reviews.first().user
            reviews.first().reviews.forEach { e -> e.user = dbUser }

            if (reviews.isEmpty()) {
                warningText.text = "Non ci sono recensioni per l'utente corrente"
            } else {
                reviewsLinearLayout.removeAllViews()
                val transaction = parentFragmentManager.beginTransaction()
                reviews.first().reviews.forEach { r ->
                    val fragment = ReviewElement.newInstance(r)
                    fragment.onRefreshNeeded = { loadContent() }
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

    override fun onResume() {
        super.onResume()
        loadContent()
    }
}