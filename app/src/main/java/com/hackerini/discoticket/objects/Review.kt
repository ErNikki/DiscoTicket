package com.hackerini.discoticket.objects

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class Review : Serializable {
    var user: User = User()
    var rating: Int = (1..10).random()
    var date = SimpleDateFormat("dd/MM/yyy").format(Date())
    var description: String =
        "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
    var images = arrayOf(
        "https://www.corriere.it/methode_image/2020/08/24/Interni/Foto%20Interni%20-%20Trattate/disco-kpWG-U32002117676772MeH-656x492@Corriere-Web-Sezioni.jpg",
        "https://lastnight.it/wp-content/uploads/2017/03/tavolo-discoteca-big-1.jpg"
    )

    public fun getLongTime(): Long {
        val localDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(this.date)
        return localDate.time
    }
}