package com.hackerini.discoticket.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.User

class MyReviews : Fragment() {

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

        val areThereReviews = false
        if (areThereReviews && User.isLogged(requireContext())) {
            view.findViewById<TextView>(R.id.MyReviewEmptyWarning).visibility = View.GONE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MyReviews()
    }
}