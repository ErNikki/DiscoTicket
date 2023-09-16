package com.hackerini.discoticket.utils

import io.ktor.http.Cookie

object CookieManager {
    private var cookie : Cookie = Cookie(name="sessionid",value="")
    var url:String="https://303d-2001-b07-6469-389f-994d-dbd7-5422-8acf.ngrok-free.app/"
    var domain:String="303d-2001-b07-6469-389f-994d-dbd7-5422-8acf.ngrok-free.app"

    fun setCookie(cookie : Cookie){
        this.cookie=cookie
    }

    fun getCookie(): Cookie {
        return this.cookie
    }

}