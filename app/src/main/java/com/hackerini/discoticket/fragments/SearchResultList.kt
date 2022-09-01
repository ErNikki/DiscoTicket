package com.hackerini.discoticket.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Event
import java.util.*

private const val ARG_PARAM1 = "elemToShow"

enum class ElementToShow {
    ALL,
    CLUBS,
    EVENTS
}

class SearchResultList : Fragment() {
    private var elementToShow: ElementToShow = ElementToShow.ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            elementToShow = ElementToShow.values()[it.getInt(ARG_PARAM1)]
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_result_list, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(elementToShow: ElementToShow) =
            SearchResultList().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, elementToShow.ordinal)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val clubList = ArrayList<Club>()
        val club0 = Club()
        val club1 = Club()
        val club2 = Club()
        club0.name = "DISCO 1"
        club0.address = "Via della prova"
        club1.name = "DISCO 2"
        club1.address = "Via della vigna"
        club2.name = "DISCO 3"
        club2.address = "Via della roma"
        clubList.add(club0)
        clubList.add(club1)
        clubList.add(club2)

        val event0 = Event("Evento prova", Calendar.getInstance().time, club0)
        val event1 = Event("Evento bello", Calendar.getInstance().time, club1)

        val ft = childFragmentManager.beginTransaction()
        if (elementToShow == ElementToShow.ALL || elementToShow == ElementToShow.CLUBS) {
            for (e in clubList.shuffled()) {
                ft.add(R.id.searchResultFragLinearLayout, DiscoElement.newInstance(e))
            }
        }

        if (elementToShow == ElementToShow.ALL || elementToShow == ElementToShow.EVENTS) {
            ft.add(R.id.searchResultFragLinearLayout, EventElement.newInstance(event0))
            ft.add(R.id.searchResultFragLinearLayout, EventElement.newInstance(event1))
        }
        ft.commitNow()
    }
}