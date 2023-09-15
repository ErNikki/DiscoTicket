package com.hackerini.discoticket.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.ReviewElement
import com.hackerini.discoticket.objects.Review
import com.hackerini.discoticket.objects.User
import com.hackerini.discoticket.room.RoomManager
import com.hackerini.discoticket.utils.ReviewsManager
import com.hackerini.discoticket.utils.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyReviews : Fragment() {
    lateinit var warningText: TextView
    lateinit var reviewsLinearLayout: LinearLayout
    lateinit var scrollView :ScrollView
    lateinit var progressBar: ProgressBar

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
        scrollView=view.findViewById(R.id.MyReviewsScrollView)
        progressBar=view.findViewById(R.id.MyReviewsprogressBar)

        loadContent()
    }



    fun loadContent() {



        CoroutineScope(Dispatchers.Default).launch {
            checkWarningText()
            if (User.isLogged(requireContext())) {
                val user = UserManager.getUser()
                val reviews = ReviewsManager.downloadReviewsByUserId(user).toList()
                requireActivity().runOnUiThread {
                    val transaction = parentFragmentManager.beginTransaction()
                    reviews.forEach { r ->
                        val fragment = ReviewElement.newInstance(r, true, true)
                        fragment.onRefreshNeeded = { checkWarningText() }
                        transaction.add(reviewsLinearLayout.id, fragment)
                    }
                    progressBar.visibility=View.GONE
                    scrollView.visibility=View.VISIBLE
                    transaction.commit()
                }

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

        var reviews = listOf<Review>()

        if (User.isLogged(requireContext())) {
            requireActivity().runOnUiThread {
                val user = UserManager.getUser()
                reviews = ReviewsManager.downloadReviewsByUserId(user).toList()
                warningText.text = "Non ci sono recensioni per l'utente corrente."
            }

        }
        else
            requireActivity().runOnUiThread {
                warningText.text = "Devi effettuare l'accesso per visualizzare le tue recensioni."
            }

        requireActivity().runOnUiThread {
            if (reviews.isEmpty()) {

                warningText.visibility = View.VISIBLE
                progressBar.visibility = View.GONE

            } else {

                warningText.visibility = View.GONE

            }
        }

    }
}