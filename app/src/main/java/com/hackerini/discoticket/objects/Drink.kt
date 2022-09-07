package com.hackerini.discoticket.objects

import java.io.Serializable

class Drink (var drinkName:String, var price: Int=(8..11).random()) : Serializable{
    //var price=(8..11).random()
    /*constructor(drinkName:String, price:Int) : this(drinkName){
        this.price=price
    }*/
    companion object {
        val ingredients = mapOf<String, Array<String>>(
            "Negroni" to arrayOf("Gin", "Campari", "Vermouth", "Fetta di arancia"),
            "Spritz" to arrayOf("Aperol", "Prosecco", "Soda", "Fetta di arancia"),
            "Mojito" to arrayOf("Rum ", "Foglie di menta", "Lime", "Zucchero", "Soda"),
            "White Russian" to arrayOf("Vodka", "Liquore al caffè", "Panna fresca liquida"),
            "London Mule" to arrayOf("Gin", "Succo di lime", "Ginger beer", "Fetta di lime"),
            "Whisky sour" to arrayOf("Bourbon whiskey", "Succo di limone", "sciroppo di zucchero"),
            "Gin Lemon" to arrayOf("Gin", "Limonata", "Fetta di limone" ),
            "Gin Tonic" to arrayOf("Gin", "Acqua tonica", "Fetta di limone"),
            "Margharita" to arrayOf("Tequila", "Succo di lime", "Triple sec", "Sale fino")

        )
    }

    var drinkIngredients: Array<String>? = arrayOf("")
        get() {
            return ingredients.get(this.drinkName)
        }




}