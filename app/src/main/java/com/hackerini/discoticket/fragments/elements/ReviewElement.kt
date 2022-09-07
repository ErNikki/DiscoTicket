package com.hackerini.discoticket.fragments.elements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.Review
import com.squareup.picasso.Picasso

private const val ARG_PARAM1 = "param1"

class ReviewElement : Fragment() {
    private var review: Review? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            review = it.getSerializable(ARG_PARAM1) as Review
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

        reviewerName.text = review?.user?.name + " " + review?.user?.surname
        reviewDate.text = review?.date
        reviewContent.text = review?.description
        reviewRatingBar.rating = review?.rating!!.toFloat()
        Picasso.get().load(review?.user?.imageProfileUrl).resize(100, 100).into(reviewerImage)

    }

    companion object {
        @JvmStatic
        fun newInstance(review: Review) =
            ReviewElement().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, review)
                }
            }
    }
}