package com.hackerini.discoticket.utils

import android.util.Log
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Order
import com.hackerini.discoticket.objects.Review
import com.hackerini.discoticket.objects.User
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
import java.text.SimpleDateFormat
import java.util.Calendar


object OrderManager {

    fun insertOrder(order: Order):Boolean{

        val mapper = jacksonObjectMapper()

        val j= JSONObject()

        j.put("userId", order.userId)
        j.put("date",order.date)
        j.put("createdAt", order.createdAt )
        val nums = JSONArray()
        order.tableIdsList.forEach { e ->
            nums.put(e)
        }
        j.put("tableIdsList",nums)
        j.put("tableIds",order.tableIds)
        order.club?.let { j.put("clubId", it.id) }
        val orderDrinksJson= mapper.writeValueAsString(order.drinks)
        val orderTicketJson= mapper.writeValueAsString(order.tickets)
        j.put("drinks", orderDrinksJson)
        j.put("tickets", orderTicketJson)

        val flag=runBlocking {

            val client = HttpClient() {
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                    })
                }
            }

            val response: HttpResponse = client.post("http://192.168.1.177:8080/DiscoticketDB/insertOrder") {
                contentType(ContentType.Application.Json)
                setBody(j.toString())
            }
            client.close()

            val jsonObj = Json.parseToJsonElement(response.body())
                .jsonObject
                .toMap()


            (jsonObj.get("success")?.toString().equals("true"))


        }
        return flag
    }

    fun getOrders(user: User):Array<Order> =
        runBlocking {
            val gson = Gson()
            val client = HttpClient()

            val response: HttpResponse = client.submitForm(
                url = "http://192.168.1.177:8080/DiscoticketDB/getOrdersByUserId",
                formParameters = parameters {
                    append("id", user.id.toString())
                }
            )
            client.close()
            val listPersonType = object : TypeToken<Array<Order>>() {}.type
            val ordersArray : Array<Order> = gson.fromJson(response.body() as String, listPersonType)
            ordersArray
        }

    fun getTablesIds(club: Club, date: String):Array<Int> =
        runBlocking {
            val gson = Gson()
            val client = HttpClient()

            val response: HttpResponse = client.submitForm(
                url = "http://192.168.1.177:8080/DiscoticketDB/getTableIds",
                formParameters = parameters {
                    append("id", club.id.toString())
                    append("date",date)
                }
            )
            client.close()
            val listPersonType = object : TypeToken<Array<Int>>() {}.type
            val idsArray : Array<Int> = gson.fromJson(response.body() as String, listPersonType)
            idsArray
        }

}