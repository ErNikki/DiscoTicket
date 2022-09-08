package com.hackerini.discoticket.fragments.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.FilterCriteria
import com.hackerini.discoticket.objects.LocationType
import com.hackerini.discoticket.utils.ObjectLoader

private const val ARG_PARAM1 = "param1"

class Filter : DialogFragment(), CompoundButton.OnCheckedChangeListener,
    AdapterView.OnItemSelectedListener {
    private var filterCriteria: FilterCriteria? = null
    private var originalFilterCriteria: FilterCriteria? = null
    var onOkClicked: ((FilterCriteria) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            filterCriteria = it.getSerializable(ARG_PARAM1) as FilterCriteria
        }
        originalFilterCriteria = filterCriteria?.copy()
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
        fun newInstance(filterCriteria: FilterCriteria) =
            Filter().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, filterCriteria)
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
        val priceSlider = view.findViewById<RangeSlider>(R.id.filterPriceRange)

        val languages = resources.getStringArray(R.array.LocationType)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, languages)
        locationSpinner.adapter = adapter
        locationSpinner.onItemSelectedListener = this
        locationSpinner.setSelection(filterCriteria?.locationType!!.ordinal)

        distanceRange.value =
            filterCriteria!!.maxDistance.toFloat().coerceIn(0F, distanceRange.valueTo)

        priceSlider.values = arrayListOf(
            filterCriteria!!.priceRange.first.toFloat().coerceAtLeast(0f),
            filterCriteria!!.priceRange.second.toFloat().coerceAtMost(priceSlider.valueTo)
        )

        distanceRange.addOnChangeListener { slider, value, fromUser ->
            filterCriteria?.maxDistance = value.toInt()
            distanceText.text = value.toInt().toString() + " Km"
        }
        priceSlider.addOnChangeListener { slider, value, fromUser ->
            filterCriteria?.priceRange =
                Pair(slider.values.first().toInt(), slider.values[1].toInt())
        }

        val genres = ObjectLoader.getClubs(requireContext()).map { c -> c.musicGenres }
            .reduce { acc, strings -> acc + strings }.toSet()
        genres.forEach {
            val checkbox = CheckBox(requireContext())
            checkbox.text = it
            checkbox.tag = it
            checkbox.isChecked = filterCriteria?.genres?.find { s -> s == it } != null
            checkbox.setOnCheckedChangeListener(this)
            genresLinearLayout.addView(checkbox)
        }

        okButton.setOnClickListener {
            this.dismiss()
            onOkClicked?.invoke(filterCriteria!!)
        }
        cancelButton.setOnClickListener {
            this.dismiss()
        }

    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked)
            filterCriteria?.genres?.add(buttonView?.tag as String)
        else
            filterCriteria?.genres?.remove(buttonView?.tag as String)

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        filterCriteria?.locationType = LocationType.values()[position]
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}