package com.hackerini.discoticket.objects

import androidx.room.*
import java.io.Serializable

enum class ItemType {
    Ticket,
    Drink
}

@Entity(tableName = "OrderItem")
class OrderItem(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "quantity") val quantity: Int,
    @ColumnInfo(name = "unitaryPrice") val unitaryPrice: Float,
    @ColumnInfo(name = "type") val type: ItemType,
    @ColumnInfo(name = "orderId") var orderId: Int,
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

@Dao
interface OrderItemDao {
    @Query("SELECT * FROM OrderItem")
    fun getAll(): List<OrderItem>

    @Insert
    fun insert(orderItem: OrderItem)

    @Delete
    fun delete(orderItem: OrderItem)
}