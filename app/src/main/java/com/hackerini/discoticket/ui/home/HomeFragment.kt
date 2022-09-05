package com.hackerini.discoticket.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hackerini.discoticket.R
import com.hackerini.discoticket.databinding.FragmentHomeBinding
import com.hackerini.discoticket.fragments.elements.HomePageDiscoElement
import com.hackerini.discoticket.fragments.elements.HomePageEventElement
import com.hackerini.discoticket.utils.ObjectLoader

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
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nearYouLL = view.findViewById<LinearLayout>(R.id.HomeNearYouLinearLayout)
        val nextEventLL = view.findViewById<LinearLayout>(R.id.HomeNextEventLinearLayout)
        val lastViewedLL = view.findViewById<LinearLayout>(R.id.HomeLastViewedLinearLayout)

        val clubs = ObjectLoader.getClubs(requireContext())
        var transaction = parentFragmentManager.beginTransaction()
        clubs.forEach { club ->
            transaction.add(R.id.HomeNearYouLinearLayout,HomePageDiscoElement.newInstance(club))
            transaction.add(R.id.HomeLastViewedLinearLayout,HomePageDiscoElement.newInstance(club))
        }

        val events = ObjectLoader.getEvents(requireContext())
        events.sortBy { event->event.date.time }
        events.forEach { event ->
            transaction.add(R.id.HomeNextEventLinearLayout, HomePageEventElement.newInstance(event))
        }

        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}