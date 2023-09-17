package com.hackerini.discoticket.utils

import io.ktor.http.Cookie

object CookieManager {
    private var cookie : Cookie = Cookie(name="sessionid",value="")
    var url:String="https://1824-2001-b07-6469-389f-38f6-8a45-2d2d-3c70.ngrok-free.app/"
    var domain:String="1824-2001-b07-6469-389f-38f6-8a45-2d2d-3c70.ngrok-free.app"

    fun setCookie(cookie : Cookie){
        this.cookie=cookie
    }

    fun getCookie(): Cookie {
        return this.cookie
    }

}