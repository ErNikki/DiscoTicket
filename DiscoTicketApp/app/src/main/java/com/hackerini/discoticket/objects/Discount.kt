package com.hackerini.discoticket.objects

import androidx.room.*
import java.io.Serializable

enum class TypeOfDiscount {
    FreeDrink,
    Percentage,
    NeatValue,
    Nothing,
}

@Entity(tableName = "discount")
class Discount(
    var name: String,
    var amount: Float,
    var type: TypeOfDiscount,
    val userId: Int = 0
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

@Dao
interface DiscountDao {
    @Query("SELECT * FROM discount WHERE userId=:id")
    fun getDiscountsByUser(id: Int): List<Discount>

    @Insert
    fun insert(discount: Discount)

    @Delete
    fun delete(discount: Discount)
}