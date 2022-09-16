package com.hackerini.discoticket.objects

import androidx.room.*
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Entity
class Review(
    @PrimaryKey(autoGenerate = true) val reviewId: Int,
    @ColumnInfo val userCreatorId : Int,
    @ColumnInfo val rating : Int,
    @ColumnInfo val date: String = SimpleDateFormat("dd/MM/yyy").format(Date()),
    @ColumnInfo val description : String
    ) : Serializable {

    /*var images = arrayOf(
        "https://www.corriere.it/methode_image/2020/08/24/Interni/Foto%20Interni%20-%20Trattate/disco-kpWG-U32002117676772MeH-656x492@Corriere-Web-Sezioni.jpg",
        "https://lastnight.it/wp-content/uploads/2017/03/tavolo-discoteca-big-1.jpg"
    )*/

}


data class UserWithReviews(
    @Embedded val user : User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userCreatorId"
    )
    val reviews : List<Review>
    ):Serializable {

}


@Dao
interface ReviewDao{
    @Query("SELECT * FROM review")
    fun getAll(): List<Review>

    /*@Transaction
    @Query("SELECT * FROM ")*/

    @Insert
    fun insert()

    @Delete
    fun delete()
}
