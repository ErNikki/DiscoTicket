package com.hackerini.discoticket.fragments.elements

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
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
import com.hackerini.discoticket.activities.EventDetails
import com.hackerini.discoticket.objects.Event
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "hideDetails"

class ElementToShow {
    companion object {
        val Date = 1.shl(1)
        val Location = 1.shl(2)
    }
}

class HomePageEventElement : Fragment() {
    private var event: Event? = null
    private var elementsToShow: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            event = it.getSerializable(ARG_PARAM1) as Event
            elementsToShow = it.getInt(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_page_event_element, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.HomePageEventElementName).text = event?.name
        Picasso.get().load(event?.imgUrl).resize(250, 250)
            .into(view.findViewById<ImageView>(R.id.HomePageEventElementImage))

        val discoName = view.findViewById<TextView>(R.id.HomePageEventLocation)
        val eventDate = view.findViewById<TextView>(R.id.HomePageEventDate)

        if (elementsToShow.and(ElementToShow.Date) != 0) {
            val df = SimpleDateFormat("dd/MM", Locale.getDefault())
            val boldSpannableString = SpannableString("il " + df.format(event?.date))
            boldSpannableString.setSpan(StyleSpan(Typeface.BOLD), 3, 8, 0)
            eventDate.text = boldSpannableString
        } else {
            eventDate.visibility = View.GONE
        }
        if (elementsToShow.and(ElementToShow.Location) != 0) {
            val mSpannableString = SpannableString("Luogo: " + event?.club?.name)
            mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)
            discoName.text = mSpannableString

            discoName.setOnClickListener {
                val intent = Intent(context, ClubDetails::class.java)
                intent.putExtra("club", event?.club)
                startActivity(intent)
            }
        } else {
            discoName.visibility = View.GONE
        }
        view.findViewById<CardView>(R.id.HomePageEventCard).setOnClickListener {
            val intent = Intent(requireContext(), EventDetails::class.java)
            intent.putExtra("event", event)
            startActivity(intent)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(
            event: Event,
            elementsToShow: Int = ElementToShow.Date.or(ElementToShow.Location)
        ) =
            HomePageEventElement().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, event)
                    putInt(ARG_PARAM2, elementsToShow)
                }
            }
    }
}