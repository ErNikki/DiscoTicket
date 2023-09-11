package com.hackerini.discoticket.objects

import java.io.Serializable
import java.util.*

data class Order(
    val createdAt: Long
) : Serializable {
    constructor() : this(Calendar.getInstance().time.time)
    val id: Int = 0

    var userId: Int = 0

    var date: String = ""

    var tableIds: String = ""

    var clubId: Int = -1

    var discount: Float = 0F

    var tableIdsList: List<Int> = ArrayList()

    val drinks = ArrayList<OrderItem>()

    val tickets = ArrayList<OrderItem>()

    var club: Club? = null

    var appliedDiscount: Discount? = null

    fun getAllElements(): List<OrderItem> {
        return drinks + tickets
    }

    fun getNumberOfItems():Int{
        var amount = 0
        drinks.forEach { item ->
            amount += item.quantity
        }
        tickets.forEach { item ->
            amount += item.quantity
        }
        return amount
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

    fun includeTickets(): Boolean {
        return tickets.isNotEmpty()
    }

    //Call it before the storage
    fun prepare() {
        clubId = this.club?.id!!
        tableIds = tableIdsList.joinToString(",")
        for (e in tickets)
            e.type = ItemType.Ticket
        for (e in drinks)
            e.type = ItemType.Drink
    }
}




