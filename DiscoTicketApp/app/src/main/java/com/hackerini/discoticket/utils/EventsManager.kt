package com.hackerini.discoticket.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Event
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.runBlocking

object EventsManager {

    private var events : Array<Event> = arrayOf<Event>()

    fun downloadEvents(){
        runBlocking {
            val gson = Gson()
            val client = HttpClient()
            val response: HttpResponse = client.get(CookieManager.url+"DiscoticketDB/getEvents")
            client.close()
            val listPersonType = object : TypeToken<Array<Event>>() {}.type
            val a_events: Array<Event> = gson.fromJson(response.body() as String, listPersonType)
            val clubs = ClubsManager.getClubs()
            for (event in a_events) {
                event.club = clubs.first { club -> club.id == event.clubId }
            }
            events=a_events
        }
    }

    fun getEvents(): Array<Event>{
        return events
    }
}