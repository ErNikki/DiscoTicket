package com.hackerini.discoticket.objects

import android.graphics.Color
import java.io.Serializable

class Club : Serializable {
    var name: String = ""
    var address: String = ""
    var rating: Float = (10..50).random()/10F
    var reviewAmount = (10..100).random()
    var imgUrl: String = "https://img.freepik.com/free-vector/disco-ball-background_1284-5130.jpg"
    var reviews = arrayOf(Review(), Review())
    var description =
        "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."

    var labels = arrayOf("")
        get() {
            val a = arrayOf("EDM", "Outdoor", "Techno", "Reggaeton")
            a.shuffle()
            return a.take(2).toTypedArray()
        }

    companion object {
        fun getLabelColorFromName(labelName: String): Int {
            return when (labelName) {
                "EDM" -> Color.argb(180, 255, 128, 128)
                "Outdoor" -> Color.argb(180, 128, 255, 128)
                "Techno" -> Color.argb(180, 80, 80, 160)
                "Reggaeton" -> Color.argb(180, 200, 80, 200)
                "Free entry" -> Color.argb(180, 200, 255, 200)
                "Rock" -> Color.argb(120, 255, 255, 255)
                else -> Color.argb(180, 255, 255, 255)
            }
        }
    }


}