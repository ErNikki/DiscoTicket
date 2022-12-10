package com.hackerini.discoticket.fragments.elements

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.hackerini.discoticket.R
import com.hackerini.discoticket.activities.ClubDetails
import com.hackerini.discoticket.objects.Club
import com.squareup.picasso.Picasso

private const val ARG_PARAM1 = "disco"
private const val ARG_PARAM2 = "showDistance"

class HomePageDiscoElement : Fragment() {
    private var club: Club? = null
    private var showDistance: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            club = it.getSerializable(ARG_PARAM1) as Club
            showDistance = it.getBoolean(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_page_disco_element, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.HomePageDiscoElementName).text = club?.name
        Picasso.get().load(club?.imgUrl).resize(250, 250)
            .into(view.findViewById<ImageView>(R.id.HomePageDiscoElementImage))

        view.findViewById<CardView>(R.id.HomePageDiscoCard).setOnClickListener {
            val intent = Intent(requireContext(), ClubDetails::class.java)
            intent.putExtra("club", club)
            startActivity(intent)
        }

        val distanceText = view.findViewById<TextView>(R.id.HomePageDiscoElementDistance)
        if (showDistance) {
            val mSpannableString = SpannableString("Distanza: ${club?.distanceFromYou}km")
            mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)
            distanceText.text = mSpannableString
            distanceText.visibility = View.VISIBLE
        } else {
            distanceText.visibility = View.GONE
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(club: Club, showDistance: Boolean = false) =
            HomePageDiscoElement().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, club)
                    putSerializable(ARG_PARAM2, showDistance)
                }
            }
    }
}