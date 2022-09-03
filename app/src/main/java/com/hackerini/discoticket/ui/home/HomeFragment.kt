package com.hackerini.discoticket.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hackerini.discoticket.R
import com.hackerini.discoticket.activities.BuyTicket
import com.hackerini.discoticket.activities.DrinkMenu
import com.hackerini.discoticket.activities.Payment
import com.hackerini.discoticket.activities.SearchResult
import com.hackerini.discoticket.databinding.FragmentHomeBinding
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.OrderPreview
import kotlinx.coroutines.channels.TickerMode

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button = view.findViewById<Button>(R.id.button3)
        val buttonDrink = view.findViewById<Button>(R.id.buttonDrinkMenu)
        val buttonPayment = view.findViewById<Button>(R.id.buttonPayment)
        val buttonTicket = view.findViewById<Button>(R.id.buttonTicketHome)

        button?.setOnClickListener {
            val intent = Intent(activity, SearchResult::class.java)
            startActivity(intent)
        }
        buttonDrink?.setOnClickListener {
            val intent = Intent(activity, DrinkMenu::class.java)
            startActivity(intent)
        }
        buttonPayment?.setOnClickListener {
            val intent = Intent(activity, Payment::class.java)
            intent.putExtra("OrderPreview", OrderPreview())
            startActivity(intent)
        }
        buttonTicket?.setOnClickListener {
            val club = Club()
            club.name = "DISCO 1"
            club.address = "Via della prova"
            val intent = Intent(activity, BuyTicket::class.java)
            intent.putExtra("club", club)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}