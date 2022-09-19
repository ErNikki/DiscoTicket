package com.hackerini.discoticket.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.DiscoElement
import com.hackerini.discoticket.fragments.elements.EventElement
import com.hackerini.discoticket.fragments.views.Filter
import com.hackerini.discoticket.objects.*
import com.hackerini.discoticket.utils.ObjectLoader
import java.util.*

class SearchResult : AppCompatActivity(), AdapterView.OnItemSelectedListener, TextWatcher {
    private var filterCriteria = FilterCriteria()

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
        locationSpinner.onItemSelectedListener = this

        val searchText = findViewById<TextInputEditText>(R.id.SearchResultSearchText)
        searchText.requestFocus()

        filterButton.setOnClickListener {
            val filterFragment = Filter.newInstance(filterCriteria)
            filterFragment.onOkClicked = { a ->
                this.filterCriteria = a
                loadContent()
            }
            filterFragment.show(supportFragmentManager, "prova")
        }

        val discoChip = findViewById<Chip>(R.id.searchResultClubChip)
        val eventChip = findViewById<Chip>(R.id.searchResultEventChip)

        discoChip.setOnCheckedChangeListener { _, _ ->
            filterCriteria.elementToShow =
                getElementToShow(discoChip.isChecked, eventChip.isChecked)
            loadContent()
        }
        eventChip.setOnCheckedChangeListener { _, _ ->
            filterCriteria.elementToShow =
                getElementToShow(discoChip.isChecked, eventChip.isChecked)
            loadContent()
        }
        searchText.addTextChangedListener(this)
        filterCriteria.elementToShow = ElementToShow.ALL
        loadContent()

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

    private fun loadContent() {
        val clubList = ObjectLoader.getClubs(applicationContext)
        val events = ObjectLoader.getEvents(applicationContext)

        val fragments = supportFragmentManager.fragments
        val ft = supportFragmentManager.beginTransaction()

        for (fragment in fragments) {
            ft.remove(fragment)
        }

        val elements = LinkedList<Any>()

        //Filter by club or event
        val query = filterCriteria.query
        val elementToShow = filterCriteria.elementToShow
        if (elementToShow == ElementToShow.ALL || elementToShow == ElementToShow.CLUBS) {
            for (e in clubList) {
                if (query.isBlank())
                    elements.add(e)
                else if (query.isNotBlank() && e.name.contains(query, true)) {
                    elements.add(e)
                }
            }
        }

        if (elementToShow == ElementToShow.ALL || elementToShow == ElementToShow.EVENTS) {
            for (e in events) {
                if (query.isBlank())
                    elements.add(e)
                else if (query.isNotBlank() && e.name.contains(query, true)) {
                    elements.add(e)
                }
            }
        }

        filterCriteria.filter(elements).forEach { item ->
            if (item is Club)
                ft.add(R.id.searchResultLinearLayout, DiscoElement.newInstance(item))
            else if (item is Event)
                ft.add(R.id.searchResultLinearLayout, EventElement.newInstance(item))

        }
        ft.commitNow()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        filterCriteria.orderCriteria = OrderCriteria.values()[position]
        loadContent()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        filterCriteria.query = s.toString()
        loadContent()
    }

    override fun afterTextChanged(s: Editable?) {
    }


}