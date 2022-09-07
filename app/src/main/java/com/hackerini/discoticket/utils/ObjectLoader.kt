package com.hackerini.discoticket.utils

import android.content.Context
import android.os.Environment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Drink
import com.hackerini.discoticket.objects.Event
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

        fun getClubs(context: Context): Array<Club> {
            val gson = Gson()
            val jsonFileString = getJsonDataFromAsset(context, "clubs.json")
            val listPersonType = object : TypeToken<Array<Club>>() {}.type
            return gson.fromJson(jsonFileString, listPersonType)
        }

        fun getEvents(context: Context): Array<Event> {
            val gson = Gson()
            val jsonFileString = getJsonDataFromAsset(context, "events.json")
            val listPersonType = object : TypeToken<Array<Event>>() {}.type
            val events: Array<Event> = gson.fromJson(jsonFileString, listPersonType)
            val clubs = getClubs(context)
            for (event in events) {
                event.club = clubs.first { club -> club.id == event.clubId }
            }
            return events
        }

        fun getDrinks(context: Context, club: Club): Array<Drink> {
            val gson = Gson()
            val jsonFileString = getJsonDataFromAsset(context, "drinks.json")
            val listPersonType = object : TypeToken<Array<Drink>>() {}.type
            val list = gson.fromJson(jsonFileString, listPersonType) as Array<Drink>
            for (e in list)
                e.club = club
            return list
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