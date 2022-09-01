package com.hackerini.discoticket.objects

import java.io.Serializable

class Club : Serializable {
    var name: String = ""
    var address: String = ""
    var rating: Float = 0.0F
    var reviewAmount = (10..100).random()
    var imgUrl: String = "https://img.freepik.com/free-vector/disco-ball-background_1284-5130.jpg"
}