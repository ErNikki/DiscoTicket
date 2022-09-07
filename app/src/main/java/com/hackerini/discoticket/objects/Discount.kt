package com.hackerini.discoticket.objects

enum class TypeOfDiscount {
    FreeDrink,
    Percentage,
    NeatValue,
    Nothing,
}

class Discount(var name: String, var amount: Float, var type: TypeOfDiscount) {
}