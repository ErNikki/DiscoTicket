package com.hackerini.discoticket.fragments.elements

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Drink
import java.io.Serializable

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DrinkElement.newInstance] factory method to
 * create an instance of this fragment.
 */
class DrinkElement : Fragment() {
    // TODO: Rename and change types of parameters
    private var drink: Drink? = null
    private var counter= 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            drink = it.getSerializable(ARG_PARAM1) as Drink

        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drink_element, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DrinkElement.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Serializable) =
            DrinkElement().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var drinkName= view.findViewById<TextView>(R.id.drinkElementDrinkName)
        var ingredients= view.findViewById<TextView>(R.id.drinkElementIngredients)
        var price=view.findViewById<TextView>(R.id.drinkElementDrinkPrice)
        var numberOfDrinkSelected=view.findViewById<TextView>(R.id.drinkElementDrinkQuantity)

        drinkName.setText(drink?.drinkName)
        price.setText(drink?.price.toString())

        drink?.drinkIngredients?.forEach { e ->

            val builder = StringBuilder()
            builder.append(ingredients.text.toString())
                .append(", ")
                .append(e)
            ingredients.text=builder.toString()

        }


        var decreaseButton = view.findViewById<Button>(R.id.drinkElementDecreaseButton)
        decreaseButton.setOnClickListener {

            if(counter>0) {
                counter--
                numberOfDrinkSelected.setText(counter.toString())
            }

        }

        var increaseButton= view.findViewById<Button>(R.id.drinkElementIncreaseButton)
        increaseButton.setOnClickListener {
            counter++
            numberOfDrinkSelected.setText(counter.toString())
        }

    }
}