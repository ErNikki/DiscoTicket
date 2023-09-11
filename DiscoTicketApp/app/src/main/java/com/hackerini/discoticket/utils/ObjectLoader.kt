package com.hackerini.discoticket.utils

import android.content.Context
import android.os.Environment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Drink
import com.hackerini.discoticket.objects.Event
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.parameters
import kotlinx.coroutines.runBlocking
import java.io.FileWriter
import java.io.IOException
import java.io.Serializable

class ObjectLoader {
    companion object {
        private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
            val jsonString: String
            try {
                jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                return null
            }
            return jsonString
        }

        fun getClubs(context: Context): Array<Club> = runBlocking {
            val gson = Gson()
            //val jsonFileString = getJsonDataFromAsset(context, "clubs.json")
            val client = HttpClient ()
            val response: HttpResponse = client.get("http://192.168.1.177:8080/DiscoticketDB/getClubs")
            client.close()
            val listPersonType = object : TypeToken<Array<Club>>() {}.type
            val clubs : Array<Club> = gson.fromJson(response.body() as String, listPersonType)

            clubs
        }

        fun getClub(id : Int) : Club = runBlocking {
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


        fun getEvents(context: Context): Array<Event> = runBlocking {
            val gson = Gson()
            val client = HttpClient()
            val response: HttpResponse = client.get("http://192.168.1.177:8080/DiscoticketDB/getEvents")
            client.close()
            val listPersonType = object : TypeToken<Array<Event>>() {}.type
            val events: Array<Event> = gson.fromJson(response.body() as String, listPersonType)
            val clubs = getClubs(context)
            for (event in events) {
                event.club = clubs.first { club -> club.id == event.clubId }
            }
            events
        }

        fun getDrinks(context: Context, club: Club): Array<Drink> = runBlocking {
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

        fun generateJson(fileName: String, elements: Array<Serializable>) {
            val downloadPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            val fw = FileWriter(downloadPath.path + "/" + fileName)
            Gson().toJson(elements, fw)
            fw.close()

        }
    }
}