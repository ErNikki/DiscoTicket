package com.hackerini.discoticket.objects

import java.io.Serializable

class OrderItem(var name: String, var quantity: Int, var unitaryPrice: Float) : Serializable {

    //Returns the total amount for this item, it is useful when there is more than unitary quantity
    fun getTotalAmount(): Float {
        return this.quantity * this.unitaryPrice
    }
}