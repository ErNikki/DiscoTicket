package com.hackerini.discoticket.fragments.elements

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.views.QuanitySelector
import com.hackerini.discoticket.objects.Drink
import java.io.Serializable

private const val ARG_PARAM1 = "param1"

class DrinkElement : Fragment() {
    // TODO: Rename and change types of parameters
    var drink: Drink? = null
    private var name = ""
    var onQuantityChange: (() -> Unit)? = null
    var quantity = 0

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

        val drinkImage = view.findViewById<ImageView>(R.id.drinkElementDrinkIcon)
        val drinkName = view.findViewById<TextView>(R.id.drinkElementDrinkName)
        val ingredients = view.findViewById<TextView>(R.id.drinkElementIngredients)
        val priceText = view.findViewById<TextView>(R.id.drinkElementDrinkPrice)
        val numberOfDrinkSelected = view.findViewById<FrameLayout>(R.id.drinkElementQuanitySelector)

        val quantitySelector = QuanitySelector.newInstance()
        childFragmentManager.beginTransaction().add(numberOfDrinkSelected.id, quantitySelector)
            .commit()

        name = drink?.name.toString()
        drinkName.text = name

        priceText.text = String.format("%.2f", drink!!.price).plus("€/Drink")

        ingredients.text = ""
        val builder = StringBuilder()
        drink?.ingredients?.forEach { e ->
            builder.append(ingredients.text.toString())
                .append(e)
                .append(", ")
        }
        ingredients.text = builder.toString().dropLast(2)

        quantitySelector.onChangeListener = {
            quantity = it
            onQuantityChange?.invoke()
        }

        val inputStream = requireActivity().assets.open("drink_images/" + drink?.imagePath)
        val d = Drawable.createFromStream(inputStream, null)
        drinkImage.setImageDrawable(d)
    }
}