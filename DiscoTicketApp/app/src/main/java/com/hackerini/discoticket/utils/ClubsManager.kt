package com.hackerini.discoticket.utils

import android.location.Location
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Drink
import com.hackerini.discoticket.objects.Review
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.parameters
import kotlinx.coroutines.runBlocking

object ClubsManager {

    private var clubs : Array<Club> = arrayOf<Club>()

    fun downloadClubs(){

        runBlocking {
            val gson = Gson()
            //val jsonFileString = getJsonDataFromAsset(context, "clubs.json")
            val client = HttpClient ()
            val response: HttpResponse = client.get("http://192.168.1.177:8080/DiscoticketDB/getClubs")
            client.close()
            val listPersonType = object : TypeToken<Array<Club>>() {}.type
            clubs = gson.fromJson(response.body() as String, listPersonType)
        }

    }

    fun downloadClub(id : Int) : Club = runBlocking {
        val gson = Gson()
        val client = HttpClient()
        val response: HttpResponse = client.submitForm(
            url = "http://192.168.1.177:8080/DiscoticketDB/getClubById",
            formParameters = parameters {
                //append("id", review?.clubId.toString())
                append("id", id.toString())
            }
        )
        client.close()
        val listPersonType = object : TypeToken<Club>() {}.type
        gson.fromJson(response.body() as String, listPersonType)


    }

    fun downloadDrinks(club: Club) : Array<Drink> = runBlocking {
            val gson = Gson()
            val client = HttpClient()
            val response: HttpResponse = client.get("http://192.168.1.177:8080/DiscoticketDB/getDrinks")
            client.close()
            val listPersonType = object : TypeToken<Array<Drink>>() {}.type
            val list = gson.fromJson(response.body() as String, listPersonType) as Array<Drink>
            for (e in list)
                e.club = club
            list
        }


    fun getClubs(): Array<Club>{
        return clubs
    }



    fun computeDistance(location: Location?){
        if(location==null){
            return
        }
        clubs.forEach {
            c ->
            val endPoint = Location("locationA")
            endPoint.latitude = c.gpsCords[0].toDouble()
            endPoint.longitude = c.gpsCords[1].toDouble()
            val distance: Float = location.distanceTo(endPoint)
            Log.d("distance",(distance/1000).toString())
            c.distanceFromYou= (distance/1000).toInt()


        }
    }

}