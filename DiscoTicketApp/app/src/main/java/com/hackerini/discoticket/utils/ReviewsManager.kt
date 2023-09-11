package com.hackerini.discoticket.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Review
import com.hackerini.discoticket.objects.User
import com.squareup.picasso.Picasso

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry


object ReviewsManager {
    fun insert(review: Review){
        runBlocking {

            val client = HttpClient() {
            }

            val response: HttpResponse = client.submitForm(
                url = "http://192.168.1.177:8080/DiscoticketDB/insertReview",
                formParameters = parameters {
                    append("userId", review.user.id.toString())
                    append("clubId", review.clubId.toString())
                    append("date", review.date)
                    append("description", review.description)
                    append("rating", review.rating.toString())
                    append("images",review.images.toString())
                }
            )
            client.close()
            val jsonObj = Json.parseToJsonElement(response.body())
                .jsonObject
                .toMap()
            if (jsonObj.get("success")?.toString().equals("true")) {
                jsonObj.get("id").toString().toInt()
            }
            else{
                -1
            }
        }
    }

    fun insert2(review: Review):Boolean =
        runBlocking {

            val client = HttpClient() {
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                    })
                }
            }
            val j= JSONObject()
            j.put("userId", review.user.id)
            j.put("clubId", review.clubId)
            j.put("date", review.date)
            j.put("description", review.description)
            j.put("rating", review.rating)

            val img = JSONArray()
            review.aux.forEach {
                    i->
                    img.put(encodeTobase64(i))
            }
            j.put("images",img)

            val response: HttpResponse = client.post("http://192.168.1.177:8080/DiscoticketDB/insertReview2") {
                contentType(ContentType.Application.Json)
                setBody(j.toString())
            }
            client.close()
            val jsonObj = Json.parseToJsonElement(response.body())
                .jsonObject
                .toMap()
            jsonObj.get("success")?.toString().equals("true")

        }


    fun encodeTobase64(image: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 60, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun decodeBase64(input: String?): Bitmap? {
        val decodedByte = Base64.decode(input, 0)
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
    }

    fun delete(review: Review){
        runBlocking {
            val client = HttpClient()
            val response: HttpResponse = client.submitForm(
                url = "http://192.168.1.177:8080/DiscoticketDB/deleteReview",
                formParameters = parameters {
                    append("id", review.id.toString())
                }
            )
            client.close()
            val jsonObj = Json.parseToJsonElement(response.body())
                .jsonObject
                .toMap()
            jsonObj.get("success")?.toString().equals("true")
        }
    }

    fun editReview(review: Review):Boolean=
        runBlocking {
            Log.d("onEdit",review.id.toString())
            Log.d("onEdit",review.description.toString())
            val client = HttpClient()
            val response: HttpResponse = client.submitForm(
                url = "http://192.168.1.177:8080/DiscoticketDB/editReview",
                formParameters = parameters {
                    append("id", review.id.toString())
                    append("description", review.description)
                    append("rating", review.rating.toString())
                    append("images",review.images.toString())
                }
            )
            client.close()
            val jsonObj = Json.parseToJsonElement(response.body())
                .jsonObject
                .toMap()
            Log.d("editReview",jsonObj.get("message").toString())
            jsonObj.get("success")?.toString().equals("true")



        }

    fun editReview2(review: Review):Boolean=
        runBlocking {

            val client = HttpClient() {
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                    })
                }
            }
            val j= JSONObject()
            j.put("id", review.id)
            j.put("description", review.description)
            j.put("rating", review.rating)

            val img = JSONArray()
            review.aux.forEach {
                    i->
                img.put(encodeTobase64(i))
            }
            j.put("images",img)

            val response: HttpResponse = client.post("http://192.168.1.177:8080/DiscoticketDB/editReview2") {
                contentType(ContentType.Application.Json)
                setBody(j.toString())
            }
            client.close()
            val jsonObj = Json.parseToJsonElement(response.body())
                .jsonObject
                .toMap()
            jsonObj.get("success")?.toString().equals("true")
        }

    fun downloadReviewsByUserId(user: User):Array<Review> =
        runBlocking {
            val gson = Gson()
            val client = HttpClient()

            val response: HttpResponse = client.submitForm(
                url = "http://192.168.1.177:8080/DiscoticketDB/getReviewsByUserId",
                formParameters = parameters {
                    append("id", user.id.toString())
                }
            )
            client.close()
            val listPersonType = object : TypeToken<Array<Review>>() {}.type
            val reviewsArray : Array<Review> = gson.fromJson(response.body() as String, listPersonType)
            /*
            for (review in reviewsArray) {
                review.images.forEach {
                        i->
                    review.aux.plus(Picasso.get().load(i).get())
                }
            }*/
            reviewsArray
        }
    fun downloadReviewByClub(club: Club): Array<Review> =
    //val reviewsByClub =
        //    RoomManager(context).db.reviewDao().getAllReviewsByClub(this.id)

        runBlocking {
            val gson = Gson()
            val client = HttpClient()

            val response: HttpResponse = client.submitForm(
                url = "http://192.168.1.177:8080/DiscoticketDB/getReviewsByClubId",
                formParameters = parameters {
                    append("id", club.id.toString())
                }
            )
            client.close()
            val listPersonType = object : TypeToken<Array<Review>>() {}.type
            val reviewsArray : Array<Review> = gson.fromJson(response.body() as String, listPersonType)
            /*
            for (review in reviewsArray) {
                review.images.forEach {
                    i->
                    review.aux.plus(Picasso.get().load(i).get())
                }
            }*/
            reviewsArray
        }

}