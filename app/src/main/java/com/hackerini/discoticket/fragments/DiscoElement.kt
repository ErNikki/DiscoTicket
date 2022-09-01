package com.hackerini.discoticket.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.hackerini.discoticket.R
import com.hackerini.discoticket.activities.ClubDetails
import com.hackerini.discoticket.objects.Club
import com.squareup.picasso.Picasso
import java.io.Serializable

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [DiscoElement.newInstance] factory method to
 * create an instance of this fragment.
 */
class DiscoElement : Fragment() {
    // TODO: Rename and change types of parameters
    private var club: Club? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            club = it.getSerializable(ARG_PARAM1) as Club
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_disco_element, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment DiscoElement.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Serializable) =
            DiscoElement().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val discoName = view.findViewById<TextView>(R.id.discoElementName)
        val discoAddress = view.findViewById<TextView>(R.id.discoElementAddress)
        val discoRating = view.findViewById<RatingBar>(R.id.discoElementRating)
        val discoRatingAvg = view.findViewById<TextView>(R.id.discoElementReviewAvg)
        val image = view.findViewById<ImageView>(R.id.discoElementImage)
        val card = view.findViewById<CardView>(R.id.DiscoElementCard)

        discoName.setText(club?.name)
        discoAddress.setText(club?.address)
        discoRating.rating = club?.rating!!
        discoRatingAvg.setText(club?.rating.toString())
        Picasso.get().load(club?.imgUrl).resize(300, 300).into(image)

        card.setOnClickListener {
            val intent = Intent(activity, ClubDetails::class.java)
            intent.putExtra("club", club)
            startActivity(intent)
        }
    }
}