package com.hackerini.discoticket.utils

import io.ktor.http.Cookie

object CookieManager {
    private var cookie : Cookie = Cookie(name="sessionid",value="")

    fun setCookie(cookie : Cookie){
        this.cookie=cookie
    }

    fun getCookie(): Cookie {
        return this.cookie
    }

}