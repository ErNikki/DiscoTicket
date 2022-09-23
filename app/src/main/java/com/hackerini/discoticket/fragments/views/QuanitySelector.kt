package com.hackerini.discoticket.fragments.views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.hackerini.discoticket.R
import kotlin.math.max


class QuanitySelector : Fragment(), TextWatcher {

    lateinit var decreaseButton: Button
    lateinit var increaseButton: Button
    lateinit var editText: EditText
    private var currentSelection = 0
    var onChangeListener: ((Int) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quanity_selector, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        decreaseButton = view.findViewById(R.id.quantitySelectorMinus)
        increaseButton = view.findViewById(R.id.quantitySelectorPlus)
        editText = view.findViewById(R.id.quantitySelectorText)

        editText.setText(currentSelection.toString())

        increaseButton.setOnClickListener {
            currentSelection++
            editText.setText(currentSelection.toString())
            onChangeListener?.invoke(currentSelection)
        }
        decreaseButton.setOnClickListener {
            if (currentSelection > 0) {
                currentSelection--
                editText.setText(currentSelection.toString())
                onChangeListener?.invoke(currentSelection)
            }
        }

        editText.addTextChangedListener(this)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            QuanitySelector()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        currentSelection = s.toString().toIntOrNull() ?: 0
        currentSelection = max(currentSelection, 0)
        onChangeListener?.invoke(currentSelection)
    }

    override fun afterTextChanged(s: Editable?) {
    }
}