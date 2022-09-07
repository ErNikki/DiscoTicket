package com.hackerini.discoticket.objects

import android.util.Log
import androidx.room.*
import java.io.Serializable
import java.util.*

@Entity(tableName = "Order")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "createdAt") val createdAt: Long,
    @ColumnInfo(name = "userId") val userId: Int,
) : Serializable {
    constructor() : this(0, Calendar.getInstance().time.time, 1)

    @ColumnInfo(name = "date")
    var date: String = ""

    @ColumnInfo(name = "table")
    var tableIds: String = ""

    @ColumnInfo(name = "clubId")
    var clubId: Int = -1

    @ColumnInfo(name = "discount")
    var discount: Float = 0F

    @Ignore
    var tableIdsList: List<Int> = ArrayList()

    @Ignore
    val drinks = ArrayList<OrderItem>()

    @Ignore
    val tickets = ArrayList<OrderItem>()

    @Ignore
    var club: Club? = null

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

    //Call it before the storage
    fun prepare() {
        Log.d("CLUB", "CLUB " + club?.id)
        clubId = this.club?.id!!
        tableIds = tableIdsList.joinToString(",")
    }
}

data class OrderWithOrderItem(
    @Embedded val order: Order,
    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val items: List<OrderItem>
) : Serializable {
    fun getTotalAmount(): Float {
        return items.map { e -> e.quantity * e.unitaryPrice }.sum() - order.discount
    }

    fun includeTickets(): Boolean {
        return items.any { item -> item.type == ItemType.Ticket }
    }

    fun getTotalQuantity(): Int {
        return items.sumOf { item -> item.quantity }
    }

    fun includeDrinks(): Boolean {
        return !includeTickets()
    }
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM `Order`")
    fun getAll(): List<Order>

    @Transaction
    @Query("SELECT * FROM `Order` order by createdAt DESC")
    fun getAllOrderWithOrderItem(): List<OrderWithOrderItem>

    @Transaction
    @Query("SELECT * FROM `Order` WHERE id=:id LIMIT 1")
    fun getOrderWithOrderItem(id: Int): OrderWithOrderItem

    @Query("SELECT id FROM `Order` order by id DESC LIMIT 1")
    fun getLastId(): Int

    @Insert
    fun insert(order: Order)

    @Delete
    fun delete(order: Order)
}
