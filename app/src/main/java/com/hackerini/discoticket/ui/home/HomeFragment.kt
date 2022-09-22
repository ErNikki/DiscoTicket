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
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Event
import com.hackerini.discoticket.utils.ObjectLoader
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var clubs: Array<Club>
    private lateinit var lastViewedLL: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        clubs = ObjectLoader.getClubs(requireContext())
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lastViewedLL = view.findViewById(R.id.HomeLastViewedLinearLayout)

        var transaction = parentFragmentManager.beginTransaction()
        clubs.sortBy { club -> club.distanceFromYou }
        clubs.take(5).forEach { club ->
            transaction.add(R.id.HomeNearYouLinearLayout, HomePageDiscoElement.newInstance(club))
        }

        val events = ObjectLoader.getEvents(requireContext())
        events.filter { event -> event.date.after(Date()) }
            .sortedBy { event -> event.date.time }
            .take(5)
            .forEach { event ->
                transaction.add(
                    R.id.HomeNextEventLinearLayout,
                    HomePageEventElement.newInstance(event, true)
                )
            }

        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        lastViewedLL.removeAllViews()
        var transaction = parentFragmentManager.beginTransaction()

        Club.getLastSeen(requireContext())
            .forEach { item ->
                if (item is Club)
                    transaction.add(
                        R.id.HomeLastViewedLinearLayout,
                        HomePageDiscoElement.newInstance(item)
                    )
                else if (item is Event)
                    transaction.add(
                        R.id.HomeLastViewedLinearLayout,
                        HomePageEventElement.newInstance(item, false)
                    )
            }
        transaction.commit()
    }
}