package com.hackerini.discoticket.objects

import androidx.room.*
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Entity
class Review(
    @PrimaryKey(autoGenerate = true) val reviewId: Int,
    @ColumnInfo val userCreatorId : Int,
    ) : Serializable {
    constructor(userId: Int) : this(0, userId)

    //mi permette di creare una review vuota che viene però attribuita all'user -1 che non dovrebbe esistere
    constructor() : this(0,-1)

    var rating : Double = 0.0

    //da sitemare
    @Ignore
    val date= SimpleDateFormat("dd/MM/yyy").format(Date())

    @Ignore
    //serve perchè le recensioni vengono prese dal json
    val user=User()

    @Ignore
    var description : String =" "

    @Ignore
    var images = arrayOf(
        "https://www.corriere.it/methode_image/2020/08/24/Interni/Foto%20Interni%20-%20Trattate/disco-kpWG-U32002117676772MeH-656x492@Corriere-Web-Sezioni.jpg",
        "https://lastnight.it/wp-content/uploads/2017/03/tavolo-discoteca-big-1.jpg"
    )

}


data class UserWithReviews(
    @Embedded val user : User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userCreatorId"
    )
    val reviews : List<Review>
    ):Serializable {

}


@Dao
interface ReviewDao{
    @Query("SELECT * FROM 'Review' ")
    fun getAllReviews(): List<Review>

    @Transaction
    @Query("SELECT * FROM 'User' ")
    fun getUsersReviews(): List<UserWithReviews>

    @Transaction
    @Query("SELECT * FROM 'User' Where id=:id LIMIT 1")
    fun getAllReviewsOfUser(id : Int): List<UserWithReviews>

    @Insert
    fun insert(review : Review)

    @Delete
    fun delete(review : Review)
}
