package com.hackerini.discoticket.utils

import android.view.View
import com.hackerini.discoticket.activities.ForgotPassword
import com.hackerini.discoticket.activities.ReSendConfirmationEmail
import com.hackerini.discoticket.objects.Review
import com.hackerini.discoticket.objects.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.cookies.ConstantCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Cookie
import io.ktor.http.parameters
import io.ktor.http.setCookie
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

object UserManager {
    private var user : User = User()
    private var sessionCookie : Cookie = Cookie(name="sessionid",value="")
    private var reviews : List<Review> = listOf<Review>()

    fun getUser():User{
        return user
    }

    fun signUp(username: String, email: String, password: String, name:String, surname:String): Pair<Boolean,String> =
        runBlocking{
            val client = HttpClient()
            val response: HttpResponse = client.submitForm(
                url = "http://192.168.1.177:8080/AccountsManager/registration",
                formParameters = parameters {
                    append("username", email)
                    append("email", email)
                    append("password", password)
                    append("name", name)
                    append("surname", surname)
                }
            )
            client.close()
            val jsonObj = Json.parseToJsonElement(response.body())
                .jsonObject
                .toMap()


            if (jsonObj.get("success")?.toString().equals("true")) {
                Pair(true,"")
            }
            else{
                Pair(false,jsonObj.get("message").toString())
            }



        }

    fun login(username:String, password: String) : Pair<Boolean,String> =

        runBlocking{
            val client = HttpClient{
                install(HttpCookies)
            }
            val response: HttpResponse = client.submitForm(
                url = "http://192.168.1.177:8080/AccountsManager/login",
                formParameters = parameters {
                    append("username", username)
                    append("password", password)

                }
            )
            val jsonObj = Json.parseToJsonElement(response.body())
                .jsonObject
                .toMap()

            client.close()


            if (jsonObj.get("success")?.toString().equals("true")) {

                CookieManager.setCookie(response.setCookie().first())
                sessionCookie=response.setCookie().first()
                val id= jsonObj.get("id").toString().toInt()
                val name=jsonObj.get("name").toString()
                val surname=jsonObj.get("surname").toString()
                user.name=name
                user.id=id
                user.surname=surname
                Pair(true,"")

            }
            else{
                Pair(false,jsonObj.get("message").toString())
                //showErrorDialog(jsonObj.get("message").toString())
                //errorMessage.visibility = View.VISIBLE
            }



        }

    fun logout(){

        runBlocking {
            val cookie = CookieManager.getCookie()
            val client = HttpClient {
                install(HttpCookies) {
                    storage =
                        ConstantCookiesStorage(Cookie(name = cookie.name, value = cookie.value, domain="192.168.1.177"))
                }
            }
            val response: HttpResponse =
                client.get("http://192.168.1.177:8080/AccountsManager/logout")
            client.close()

        }
    }

    fun getReviews(){

    }

    fun updateReviews(){

    }

    fun getUserInfo(userId : Int):User=
        runBlocking {
            val client = HttpClient()
            val response: HttpResponse = client.submitForm(
                url = "http://192.168.1.177:8080/AccountsManager/getLoggedUser",
                formParameters = parameters {
                    append("id", userId.toString())
                }
            )
            client.close()
            val jsonObj = Json.parseToJsonElement(response.body())
                .jsonObject
                .toMap()
            val id= jsonObj.get("id")?.toString()?.toInt()
            val name=jsonObj.get("name")?.toString()
            val surname=jsonObj.get("surname")?.toString()
            val email=jsonObj.get("email")?.toString()
            val user=User()
            if (id != null) {
                user.id=id
            }
            if (name != null) {
                user.name=name
            }
            if (surname != null) {
                user.surname=surname
            }
            if (email != null) {
                user.email=email
            }
            user
    }

    fun isUserLogged():Boolean =
        runBlocking {
            val cookie= CookieManager.getCookie()
            val client = HttpClient{
                install(HttpCookies){
                    storage = ConstantCookiesStorage(Cookie(name=cookie.name, value = cookie.value, domain = "192.168.1.177"))
                }
            }
            val response: HttpResponse = client.get("http://192.168.1.177:8080/AccountsManager/isSessionActive")
            val jsonObj = Json.parseToJsonElement(response.body())
                .jsonObject
                .toMap()
            client.close()

            jsonObj.get("success")?.toString().equals("true")
        }
}