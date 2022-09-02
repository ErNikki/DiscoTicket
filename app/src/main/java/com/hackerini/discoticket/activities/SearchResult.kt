package com.hackerini.discoticket.activities

import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.DiscoElement
import com.hackerini.discoticket.fragments.EventElement
import com.hackerini.discoticket.fragments.Filter
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Event
import java.util.*


enum class ElementToShow {
    ALL,
    CLUBS,
    EVENTS,
    NONE
}

class SearchResult : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        val filterButton = findViewById<Button>(R.id.SearchResultFilterButton)

        //Update the search text with the one from the previous activity
        val searchText = findViewById<EditText>(R.id.SearchResultSearchText)
        val queryString = intent.getStringExtra("query")
        searchText.setText(queryString)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        filterButton.setOnClickListener {
            //Instead to pass the String ciao, you will pass and object with the current search criteria
            val filterFragment = Filter.newInstance("ciao")
            filterFragment.show(supportFragmentManager, "prova")
        }

        val discoChip = findViewById<Chip>(R.id.searchResultClubChip)
        val eventChip = findViewById<Chip>(R.id.searchResultEventChip)

        discoChip.setOnCheckedChangeListener { _, _ ->
            val elementToShow = getElementToShow(discoChip.isChecked, eventChip.isChecked)
            loadContent(elementToShow)
        }
        eventChip.setOnCheckedChangeListener { _, _ ->
            val elementToShow = getElementToShow(discoChip.isChecked, eventChip.isChecked)
            loadContent(elementToShow)
        }
        loadContent(ElementToShow.ALL)
    }

    private fun getElementToShow(discoChip: Boolean, eventChip: Boolean): ElementToShow {
        return if (discoChip && eventChip)
            ElementToShow.ALL
        else if (discoChip)
            ElementToShow.CLUBS
        else if (eventChip)
            ElementToShow.EVENTS
        else
            ElementToShow.NONE
    }

    private fun loadContent(elementToShow: ElementToShow) {
        val clubList = ArrayList<Club>()

        //Create some clubs
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

        val fragments = supportFragmentManager.fragments
        val ft = supportFragmentManager.beginTransaction()

        for (fragment in fragments) {
            ft.remove(fragment)
        }

        if (elementToShow == ElementToShow.ALL || elementToShow == ElementToShow.CLUBS) {
            for (e in clubList.shuffled()) {
                ft.add(R.id.searchResultLinearLayout, DiscoElement.newInstance(e))
            }
        }

        val event0 = Event("Evento prova", Calendar.getInstance().time, club0)
        val event1 = Event("Evento bello", Calendar.getInstance().time, club1)

        if (elementToShow == ElementToShow.ALL || elementToShow == ElementToShow.EVENTS) {
            ft.add(R.id.searchResultLinearLayout, EventElement.newInstance(event0))
            ft.add(R.id.searchResultLinearLayout, EventElement.newInstance(event1))
        }
        ft.commitNow()
    }


}