package com.hackerini.discoticket.fragments.elements

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.hackerini.discoticket.R
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
    private var name=""
    private var price=0


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

        val drinkImage= view.findViewById<ImageView>(R.id.drinkElementDrinkIcon)
        val drinkName= view.findViewById<TextView>(R.id.drinkElementDrinkName)
        val ingredients= view.findViewById<TextView>(R.id.drinkElementIngredients)
        val priceText=view.findViewById<TextView>(R.id.drinkElementDrinkPrice)
        val numberOfDrinkSelected=view.findViewById<TextView>(R.id.drinkElementDrinkQuantity)
        var cartTotal=requireActivity().findViewById<View>(R.id.drinkMenuCartTotal) as TextView



        name= drink?.drinkName.toString()
        drinkName.setText(name)

        price= drink?.price!!
        priceText.setText(price.toString().plus(" €"))

        ingredients.text=""
        val builder = StringBuilder()
        drink?.drinkIngredients?.forEach { e ->
            builder.append(ingredients.text.toString())
                .append(e)
                .append(", ")
        }
        ingredients.text=builder.toString().dropLast(2)

        numberOfDrinkSelected.text= counter.toString()
        var totalCartAux=0
        var auxString=""
        val decreaseButton = view.findViewById<Button>(R.id.drinkElementDecreaseButton)
        decreaseButton.setOnClickListener {

            if(counter>0) {

                counter--
                numberOfDrinkSelected.setText(counter.toString())

                auxString= cartTotal.text.toString()
                totalCartAux = auxString.dropLast(2).toInt()
                cartTotal.setText((totalCartAux- price!!).toString().plus(" €"))

            }

        }

        val increaseButton= view.findViewById<Button>(R.id.drinkElementIncreaseButton)
        increaseButton.setOnClickListener {
            counter++
            numberOfDrinkSelected.setText(counter.toString())

            auxString= cartTotal.text.toString()
            totalCartAux = auxString.dropLast(2).toInt()
            cartTotal.setText((totalCartAux+ price!!).toString().plus(" €"))
        }



    }
    fun getName():String{
        return name
    }
    fun getPrice():Int{
        return price
    }
    fun getQuantity():Int{
        return counter
    }
}