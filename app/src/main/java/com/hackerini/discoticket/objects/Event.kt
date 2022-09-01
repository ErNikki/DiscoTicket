package com.hackerini.discoticket.objects

import java.io.Serializable
import java.util.*

class Event(var name: String, var date: Date, var club: Club) : Serializable {
    var imgUrl: String =
        "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/grand-opening-event-club-bar-disco-party-ad-design-template-ee7e062b37aaa286789fc2ca1ec89db9_screen.jpg"
    var description: String =
        "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."

    var labels = arrayOf("")
        get() {
            val a = arrayOf("Free entry", "Outdoor", "Techno", "Reggaeton", "Rock")
            a.shuffle()
            return a.take(2).toTypedArray()
        }

    companion object {
        fun getLabelColorFromName(labelName: String): Int {
            return Club.getLabelColorFromName(labelName)
        }
    }
}