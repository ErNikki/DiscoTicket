package com.hackerini.discoticket.objects

import java.io.Serializable

class OrderPreview : Serializable {
    val items = arrayOf(
        OrderItem("Mojito", 3, 7.5F),
        OrderItem("Negroni", 2, 10.0F),
        OrderItem("Gin Tonic", 1, 5.0F),
        OrderItem("Gin Lemon", 1, 5.0F),
        OrderItem("Margarita", 2, 6.5F),
    )

    //Return the total amount of the order
    fun getTotalAmount(): Float {
        var amount = 0F
        items.forEach { item ->
            amount += item.getTotalAmount()
        }
        return amount
    }
}