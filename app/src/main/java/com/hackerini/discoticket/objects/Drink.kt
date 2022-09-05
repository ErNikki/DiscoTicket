package com.hackerini.discoticket.objects

import java.io.Serializable

class Drink(drinkName:String, price:Int) : Serializable{

    companion object {
        val ingredients = mapOf<String, Array<String>>(
            "Negroni" to arrayOf("Gin", "Campari", "Vermouth", "Fetta di arancia"),
            "Spritz" to arrayOf("Aperol", "Prosecco", "Soda", "Fetta di arancia"),
            "Mojito" to arrayOf("Rum ", "Foglie di menta", "Lime", "Zucchero", "Soda"),
            "White Russian" to arrayOf("Vodka", "Liquore al caffè", "Panna fresca liquida"),
            "London Mule" to arrayOf("Gin", "Succo di lime", "Ginger beer", "Fetta di lime"),
            "Whisky sour" to arrayOf("Bourbon whiskey", "Succo di limone", "sciroppo di zucchero")
        )
    }

    var drinkName: String = drinkName
    var price= price

    var drinkIngredients: Array<String>? = arrayOf("")
        get() {
            return ingredients.get(this.drinkName)
        }




}