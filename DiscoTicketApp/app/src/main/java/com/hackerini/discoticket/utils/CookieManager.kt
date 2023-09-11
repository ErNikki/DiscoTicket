package com.hackerini.discoticket.utils

import io.ktor.http.Cookie

object CookieManager {
    private var cookie : Cookie = Cookie(name="sessionid",value="")
    var url:String="http://192.168.1.177:8080/"
    var domain:String="192.168.1.177"

    fun setCookie(cookie : Cookie){
        this.cookie=cookie
    }

    fun getCookie(): Cookie {
        return this.cookie
    }

}