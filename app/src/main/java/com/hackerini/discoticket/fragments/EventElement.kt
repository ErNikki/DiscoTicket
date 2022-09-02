package com.hackerini.discoticket.fragments

import android.app.ActionBar
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.hackerini.discoticket.R
import com.hackerini.discoticket.activities.EventDetails
import com.hackerini.discoticket.objects.Event
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "event"

class EventElement : Fragment() {
    private var event: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            event = it.getSerializable(ARG_PARAM1) as Event
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_element, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Event) =
            EventElement().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val card = view.findViewById<CardView>(R.id.eventElementCard)
        val eventName = view.findViewById<TextView>(R.id.eventElementName)
        val eventAddress = view.findViewById<TextView>(R.id.eventElementAddress)
        val eventDateLocation = view.findViewById<TextView>(R.id.eventDateAnLocation)
        val image = view.findViewById<ImageView>(R.id.eventElementImage)
        val labelLayout = view.findViewById<LinearLayout>(R.id.eventElementLabelsLayout)

        eventName.setText(event?.name)
        eventAddress.setText(event?.club?.address)

        val df = SimpleDateFormat("dd/MM", Locale.getDefault())
        val eventDateLocationString = "Il " + df.format(event?.date) + " a " + event?.club?.name

        eventDateLocation.setText(eventDateLocationString)
        val imageSize = 250
        Picasso.get().load(event?.imgUrl).resize(imageSize, imageSize).into(image)

        card.setOnClickListener {
            val intent = Intent(activity, EventDetails::class.java)
            intent.putExtra("event", event)
            startActivity(intent)
        }

        val params =
            LinearLayout.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            )
        params.setMargins(0, 0, 10, 0)




        event?.labels?.forEach { e ->
            val shape = GradientDrawable()
            shape.cornerRadius = 10f
            shape.setColor(Event.getLabelColorFromName(e))

            val textview = TextView(context)
            textview.setText(e)
            textview.background = shape
            textview.setPadding(8)


            textview.layoutParams = params


            labelLayout.addView(textview)
        }
    }
}