package com.hackerini.discoticket.objects

import java.io.Serializable

class OrderPreview : Serializable {
    val items = ArrayList<OrderItem>()

    //Return the total amount of the order
    fun getTotalAmount(): Float {
        var amount = 0F
        items.forEach { item ->
            amount += item.getTotalAmount()
        }
        return amount
    }
}