package com.hackerini.discoticket.objects

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import com.hackerini.discoticket.room.FavoriteClub
import com.hackerini.discoticket.room.RoomManager
import com.hackerini.discoticket.utils.ClubsManager
import com.hackerini.discoticket.utils.EventsManager
import com.hackerini.discoticket.utils.ReviewsManager
import java.io.Serializable
import kotlin.reflect.KClass

class Club : Serializable {
    var id: Int = 0
    var name: String = ""
    var address: String = ""
    var rating: Float = 0F
    var imgUrl: String = "https://img.freepik.com/free-vector/disco-ball-background_1284-5130.jpg"
    var reviews = arrayOf<Review>()
    var description =""
    var distanceFromYou = 0
    var simpleTicketPrice = 0F
    var tableTicketPrice = 0F
    var gpsCords: Array<Float> = arrayOf(0F, 0F)
    var locationType = ""

    var musicGenres = arrayOf("")

    var labels = arrayOf("")
        get() {
            return if (locationType != "Chiuso")
                musicGenres + arrayOf("All'aperto")
            else
                musicGenres

        }

    fun getReview(context: Context): List<Review>{
        reviews=ReviewsManager.downloadReviewByClub(this)
        return reviews.toList()
        }




    fun isFavorite(context: Context): Boolean {
        val favDao = RoomManager(context).db.favoriteDao()
        return favDao.getAll().contains(FavoriteClub(this.id))
    }

    fun removeToFavorite(context: Context) {
        val favDao = RoomManager(context).db.favoriteDao()
        favDao.deleteFavorite(FavoriteClub(this.id))
    }

    fun addToFavorite(context: Context) {
        val favDao = RoomManager(context).db.favoriteDao()
        favDao.insertFavorite(FavoriteClub(this.id))
    }


    fun getClubDrinks(context: Context): Array<Drink> {
        return ClubsManager.downloadDrinks(this)
    }

    companion object {
        fun getLabelColorFromName(labelName: String): Int {
            return when (labelName) {
                "Elettronica" -> Color.argb(180, 255, 128, 128)
                "All'aperto" -> Color.argb(255, 200, 200, 200)
                "Techno" -> Color.argb(180, 80, 80, 160)
                "Reggaeton" -> Color.argb(180, 200, 80, 200)
                "Free entry" -> Color.argb(180, 200, 255, 200)
                "Rock" -> Color.argb(120, 30, 255, 255)
                else -> Color.argb(255, 200, 200, 255)
            }
        }

        fun getLastSeen(context: Context): List<Any> {
            val sharedPreferences = context.getSharedPreferences(
                "DiscoTicket",
                AppCompatActivity.MODE_PRIVATE
            )
            val clubs = ClubsManager.getClubs()
            val events = EventsManager.getEvents()
            val clubIdsString = sharedPreferences.getString("lastSeenClub", "") ?: ""
            val splittedId = clubIdsString.split(",")
            if (splittedId.any { e -> e.matches("[0-9]+".toRegex()) }) {
                val editable = sharedPreferences.edit()
                editable.remove("lastSeenClub")
                editable.apply()
                return arrayListOf<Club>()
            }
            return splittedId.filter { e -> e.isNotEmpty() }.mapNotNull { e ->
                val typeString = e.substring(0, 1)
                val id = e.substring(1).toInt()
                when (typeString) {
                    "c" -> clubs.first { club -> club.id == id }
                    else -> events.first { event -> event.id == id }
                }
            }
        }

        fun addToLastSeen(context: Context, id: Int, objectType: KClass<*>) {

            val sharedPreferences = context.getSharedPreferences(
                "DiscoTicket",
                AppCompatActivity.MODE_PRIVATE
            )
            val type = when (objectType) {
                Club::class -> "c"
                else -> "e"
            }

            val clubIdsString = sharedPreferences.getString("lastSeenClub", "") ?: ""
            val clubIds = clubIdsString.split(",").toMutableList()
            val field = type.plus(id)
            clubIds.remove(field)
            clubIds.add(0, field)

            while (clubIds.size > 5)
                clubIds.removeLast()
            val editable = sharedPreferences.edit()
            editable.putString("lastSeenClub", clubIds.joinToString(","))
            editable.apply()
        }
    }


}