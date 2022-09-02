package com.hackerini.discoticket.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.android.material.slider.Slider
import com.hackerini.discoticket.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

class Filter : DialogFragment() {
    private var param1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Filter().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locationSpinner = view.findViewById<Spinner>(R.id.filterLocationType)
        val distanceText = view.findViewById<TextView>(R.id.filterTextDistance)
        val distanceRange = view.findViewById<Slider>(R.id.filterSliderDistance)
        val genresLinearLayout = view.findViewById<LinearLayout>(R.id.filterGenreLinearLayout)
        val okButton = view.findViewById<Button>(R.id.filterOkButton)
        val cancelButton = view.findViewById<Button>(R.id.filterCancelButton)

        val languages = resources.getStringArray(R.array.LocationType)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, languages)
        locationSpinner.adapter = adapter

        distanceRange.addOnChangeListener { slider, value, fromUser ->
            distanceText.setText(value.toInt().toString() + " Km")
        }

        val genres = resources.getStringArray(R.array.genres)
        genres.forEach {
            val checkbox = CheckBox(requireContext())
            checkbox.setText(it)
            genresLinearLayout.addView(checkbox)
        }

        okButton.setOnClickListener {
            this.dismiss()
        }
        cancelButton.setOnClickListener {
            this.dismiss()
        }

    }
}