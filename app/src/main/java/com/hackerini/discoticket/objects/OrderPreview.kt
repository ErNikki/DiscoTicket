package com.hackerini.discoticket.objects

import java.io.Serializable

class OrderPreview : Serializable {
    val drinks = ArrayList<OrderItem>()
    val tickets = ArrayList<OrderItem>()

    fun getAllElements(): List<OrderItem> {
        return drinks + tickets
    }

    //Return the total amount of the order
    fun getTotalAmount(): Float {
        var amount = 0F
        drinks.forEach { item ->
            amount += item.getTotalAmount()
        }
        tickets.forEach { item ->
            amount += item.getTotalAmount()
        }
        return amount
    }
}