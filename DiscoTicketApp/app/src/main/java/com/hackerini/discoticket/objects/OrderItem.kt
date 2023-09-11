package com.hackerini.discoticket.objects

import androidx.room.*
import com.fasterxml.jackson.annotation.JsonBackReference
import java.io.Serializable

enum class ItemType {
    Ticket,
    Drink
}

class OrderItem(
    val id: Int,
    val name: String,
    val quantity: Int,
    val unitaryPrice: Float,
    var type: ItemType,
    var orderId: Int,
) : Serializable {

    constructor(name: String, quantity: Int, unitaryPrice: Float) : this(
        0,
        name,
        quantity,
        unitaryPrice,
        ItemType.Ticket,
        0
    )

    constructor(name: String, quantity: Int, unitaryPrice: Float, type: ItemType) : this(
        0,
        name,
        quantity,
        unitaryPrice,
        type,
        0
    )

    //Returns the total amount for this item, it is useful when there is more than unitary quantity
    fun getTotalAmount(): Float {
        return this.quantity * this.unitaryPrice
    }

    override fun toString(): String {
        return name + " x" + quantity.toString() + " at " + unitaryPrice.toString()
    }
}
