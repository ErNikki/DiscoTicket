package com.hackerini.discoticket.fragments.elements

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hackerini.discoticket.R
import com.hackerini.discoticket.activities.ClubDetails
import com.hackerini.discoticket.activities.QRdrinks
import com.hackerini.discoticket.objects.Order
import com.hackerini.discoticket.utils.ClubsManager
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "param1"

class PurchaseElement : Fragment() {
    private var order: Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            order = it.getSerializable(ARG_PARAM1) as Order
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_purchase_element, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = view.findViewById<TextView>(R.id.PurchaseElementTitle)
        val amount = view.findViewById<TextView>(R.id.PurchaseElementAmount)
        val date = view.findViewById<TextView>(R.id.PurchaseElementDate)
        val image = view.findViewById<ImageView>(R.id.PurchaseElementImage)
        val openQR = view.findViewById<Button>(R.id.PurchaseElementOpenQR)
        val openClub = view.findViewById<Button>(R.id.PurchaseElementInfo)

        val clubs = ClubsManager.getClubs()
        val club = clubs.first { e -> e.id == order?.clubId }

        amount.text = String.format("%.2f", order?.getTotalAmount()).plus("€")
        if (order?.includeTickets() == true) {
            title.text = "${order?.getNumberOfItems()} biglietti per ${club.name}"
            date.text =
                "Per il ".plus(order?.date?.split("-")?.reversed()?.joinToString("/"))
            image.setImageResource(R.drawable.ic_ticket)
        } else {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = order!!.createdAt
            val dataFormatter = SimpleDateFormat("dd/MM/yyyy")
            date.text = dataFormatter.format(calendar.time)
            title.text = "${order?.getNumberOfItems()} drink al ${club.name}"

        }

        openClub.setOnClickListener {
            val intent = Intent(requireContext(), ClubDetails::class.java)
            intent.putExtra("club", club)
            startActivity(intent)
        }

        openQR.setOnClickListener {
            val intent = Intent(requireContext(), QRdrinks::class.java)
            intent.putExtra("order", order)
            startActivity(intent)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(order: Order) =
            PurchaseElement().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, order)
                }
            }
    }
}