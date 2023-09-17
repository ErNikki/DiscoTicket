package com.hackerini.discoticket.utils

import android.icu.util.LocaleData
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Event
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.cookies.ConstantCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Cookie
import io.ktor.http.parameters
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale

object MeteoManager {


    fun downloadMeteo(event:Event): Pair<String,Float> =
        runBlocking {
            val dateFormatter: DateTimeFormatter =  DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val nowDate=LocalDate.now()
            val text=nowDate.format(dateFormatter)
            val parsedDate=LocalDate.parse(text,dateFormatter)

            val df = SimpleDateFormat("dd/MM/YYYY", Locale.getDefault())
            val eventdate=df.format(event.date)
            val period=Period.between(LocalDate.parse(eventdate,dateFormatter),parsedDate)
            val days=period.days

            val client = HttpClient{

            }
            val response: HttpResponse = client.get("https://api.openweathermap.org/data/2.5/weather"){
                url {
                    parameters.append("lat", event.club!!.gpsCords[0].toString() )
                    parameters.append("lon", event.club!!.gpsCords[0].toString())
                    parameters.append("appid", "a87ed1faecd9e407c3328f2350bb28e3" )
                    parameters.append("lang", "it" )
                    parameters.append("cnt",(days%16).toString())
                }

            }
            val jsonObj = Json.parseToJsonElement(response.body())
                .jsonObject
                .toMap()

            val meteoObj : JsonArray = jsonObj.get("weather") as JsonArray
            val tempObj : JsonObject = jsonObj.get("main") as JsonObject


            Log.d("meteo",(meteoObj[0] as JsonObject).get("main").toString())
            Log.d("meteo",tempObj.get("temp").toString())
            Log.d("meteo",days.toString())
            val gson = Gson()
            val temp = object : TypeToken<Float>() {}.type

            Pair((meteoObj[0] as JsonObject).get("main").toString(),tempObj.get("temp").toString().toFloat())
        }

}