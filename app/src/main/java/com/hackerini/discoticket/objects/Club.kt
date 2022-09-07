package com.hackerini.discoticket.objects

import android.content.Context
import android.graphics.Color
import com.hackerini.discoticket.room.FavoriteClub
import com.hackerini.discoticket.room.RoomManager
import java.io.Serializable

class Club : Serializable {
    var id: Int = 0
    var name: String = ""
    var address: String = ""
    var rating: Float = (10..50).random() / 10F
    var reviewAmount = (10..100).random()
    var imgUrl: String = "https://img.freepik.com/free-vector/disco-ball-background_1284-5130.jpg"
    var reviews = arrayOf(Review(), Review())
    var description =
        "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
    var avgPrice = (10..60).random()
    var distanceFromYou = (5..100).random()
    var simpleTicketPrice = (100..350).random() / 10F
    var tableTicketPrice = (500..1000).random() / 10F
    var gpsCords: Array<Float> = arrayOf(0F, 0F)
    var locationType = ""

    /*get() {
        if (field.isBlank()) {
            field = arrayOf("Aperto", "Chiuso", "Entrambi").random()
        }
        return field

    }*/
    var musicGenres = arrayOf("")

    /*get() {
        if (field[0].isBlank()) {
            val a = arrayOf("EDM", "Reggaeton", "Techno", "Rock")
            a.shuffle()
            field = a.take(2).toTypedArray()
        }
        return field

    }*/
    var labels = arrayOf("")
        get() {
            return if (locationType != "Chiuso")
                musicGenres + arrayOf("Aperto")
            else
                musicGenres

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


    var drinks = arrayOf("")
        get(){
            val listOfDrink= arrayOf("Negroni", "Spritz", "Mojito", "White Russian",
                "London Mule", "Whisky sour",
                "Gin Lemon","Gin Tonic", "Margharita"
            )
            listOfDrink.shuffle()
            field = listOfDrink.take((4..7).random()).toTypedArray()
            return field
        }

    companion object {
        fun getLabelColorFromName(labelName: String): Int {
            return when (labelName) {
                "Elettronica" -> Color.argb(180, 255, 128, 128)
                "Aperto" -> Color.argb(255, 200, 200, 200)
                "Techno" -> Color.argb(180, 80, 80, 160)
                "Reggaeton" -> Color.argb(180, 200, 80, 200)
                "Free entry" -> Color.argb(180, 200, 255, 200)
                "Rock" -> Color.argb(120, 30, 255, 255)
                else -> Color.argb(255, 200, 200, 255)
            }
        }
    }


}