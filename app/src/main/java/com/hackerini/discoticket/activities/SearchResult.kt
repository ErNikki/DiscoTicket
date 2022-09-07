package com.hackerini.discoticket.activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.DiscoElement
import com.hackerini.discoticket.fragments.elements.EventElement
import com.hackerini.discoticket.fragments.views.Filter
import com.hackerini.discoticket.utils.ObjectLoader


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
        val openMapButton = findViewById<ImageButton>(R.id.SearchResultOpenMap)
        val locationSpinner = findViewById<Spinner>(R.id.SearchResultOrderSpinner)

        val languages = resources.getStringArray(R.array.orderBy)
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languages)
        locationSpinner.adapter = adapter

        //Update the search text with the one from the previous activity
        val searchText = findViewById<EditText>(R.id.SearchResultSearchText)
        val queryString = intent.getStringExtra("query")
        searchText.setText(queryString)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

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

        openMapButton.setOnClickListener {
            startActivity(Intent(this, SearchByMap::class.java))
        }
    }

    companion object {
        fun getElementToShow(discoChip: Boolean, eventChip: Boolean): ElementToShow {
            return if (discoChip && eventChip)
                ElementToShow.ALL
            else if (discoChip)
                ElementToShow.CLUBS
            else if (eventChip)
                ElementToShow.EVENTS
            else
                ElementToShow.NONE
        }
    }

    private fun loadContent(elementToShow: ElementToShow) {
        val clubList = ObjectLoader.getClubs(applicationContext)


        val fragments = supportFragmentManager.fragments
        val ft = supportFragmentManager.beginTransaction()

        for (fragment in fragments) {
            ft.remove(fragment)
        }

        if (elementToShow == ElementToShow.ALL || elementToShow == ElementToShow.CLUBS) {
            for (e in clubList) {
                ft.add(R.id.searchResultLinearLayout, DiscoElement.newInstance(e))
            }
        }

        val event = ObjectLoader.getEvents(applicationContext)

        if (elementToShow == ElementToShow.ALL || elementToShow == ElementToShow.EVENTS) {
            ft.add(R.id.searchResultLinearLayout, EventElement.newInstance(event[0]))
            ft.add(R.id.searchResultLinearLayout, EventElement.newInstance(event[1]))
        }
        ft.commitNow()
    }


}