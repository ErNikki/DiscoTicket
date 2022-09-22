package com.hackerini.discoticket.objects

import androidx.room.*
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Entity
class Review(
    @PrimaryKey(autoGenerate = true) val reviewId: Int,
    @ColumnInfo val userCreatorId: Int,
) : Serializable {
    constructor(userId: Int) : this(0, userId)

    //mi permette di creare una review vuota che viene però attribuita all'user -1 che non dovrebbe esistere
    constructor() : this(0, -1)

    var rating: Double = 0.0
    var clubId: Int = -1

    var date = ""

    @Ignore
    var user = User()

    var description: String = ""

    @Ignore
    var images = arrayOf(
        "https://www.corriere.it/methode_image/2020/08/24/Interni/Foto%20Interni%20-%20Trattate/disco-kpWG-U32002117676772MeH-656x492@Corriere-Web-Sezioni.jpg",
        "https://lastnight.it/wp-content/uploads/2017/03/tavolo-discoteca-big-1.jpg"
    )

    fun getLongTime(): Long {
        val localDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(this.date)
        return localDate.time
    }
}

data class ReviewWithUser(
    @Embedded val review: Review,
    @Relation(
        parentColumn = "userCreatorId",
        entityColumn = "id"
    )
    val user: User
) : Serializable

data class UserWithReviews(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userCreatorId"
    )
    val reviews: List<Review>
) : Serializable


@Dao
interface ReviewDao {
    @Query("SELECT * FROM 'Review' ")
    fun getAllReviews(): List<Review>

    fun getAllReviewsByClub(clubId: Int): List<Review> {
        val reviewWithUser = getAllReviewsWithUserByClub(clubId)
        reviewWithUser.forEach { r -> r.review.user = r.user }
        return reviewWithUser.map { r -> r.review }
    }

    @Query("SELECT * FROM 'Review' WHERE clubId=:clubId")
    fun getAllReviewsWithUserByClub(clubId: Int): List<ReviewWithUser>

    @Transaction
    @Query("SELECT * FROM 'User' ")
    fun getUsersReviews(): List<UserWithReviews>

    @Transaction
    @Query("SELECT * FROM 'User' Where id=:id")
    fun getAllReviewsOfUser(id: Int): List<UserWithReviews>

    @Insert
    fun insert(review: Review)

    @Delete
    fun delete(review: Review)
}
