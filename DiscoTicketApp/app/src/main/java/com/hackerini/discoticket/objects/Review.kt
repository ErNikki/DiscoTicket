package com.hackerini.discoticket.objects

import android.graphics.Bitmap
import android.util.Log
import androidx.room.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.HttpResponse
import io.ktor.http.parameters
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*


class Review(
    var reviewId: Int,
    val userCreatorId: Int,

) : Serializable {
    constructor(userId: Int) : this(0, userId)
    constructor(user: User) : this(0, user.id) {
        this.user = user
    }

    //mi permette di creare una review vuota che viene però attribuita all'user -1 che non dovrebbe esistere
    constructor() : this(0, -1)

    var rating: Double = 0.0
    var clubId: Int = -1

    var date = ""

    var user = User()

    var description: String = ""

    var id=-1

    var aux:Array<Bitmap> = arrayOf()

    var images = arrayOf(
        "https://www.corriere.it/methode_image/2020/08/24/Interni/Foto%20Interni%20-%20Trattate/disco-kpWG-U32002117676772MeH-656x492@Corriere-Web-Sezioni.jpg",
        "https://lastnight.it/wp-content/uploads/2017/03/tavolo-discoteca-big-1.jpg"
    )

    fun getLongTime(): Long {
        val localDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(this.date)
        return localDate.time
    }


    companion object{



        fun getUserReviews(user:User){

        }
        fun getUserReviewByClubId(){

        }
    }
}


