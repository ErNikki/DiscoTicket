package com.hackerini.discoticket.objects

import java.io.Serializable
import java.security.MessageDigest
import kotlin.math.abs

class Drink(var name: String, var ingredients: Array<String>) : Serializable {
    var club: Club? = null
    var price = 0F
        get() {
            val md = MessageDigest.getInstance("MD5")
            val magicNumber = md.digest((name + club?.name + club?.id).toByteArray()).sum()
            val prices = (80..120 step 5).toList()
            return prices[abs(magicNumber % prices.size)] / 10F
        }
}