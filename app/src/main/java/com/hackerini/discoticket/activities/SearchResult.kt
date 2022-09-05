package com.hackerini.discoticket.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.DiscoElement
import com.hackerini.discoticket.fragments.elements.EventElement
import com.hackerini.discoticket.fragments.views.Filter
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Event
import com.hackerini.discoticket.utils.ObjectLoader
import java.io.File
import java.io.FileReader
import java.io.InputStream
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
        val openMapButton = findViewById<ImageButton>(R.id.SearchResultOpenMap)

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