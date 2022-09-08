package com.hackerini.discoticket.objects

import java.io.Serializable
import java.util.*

enum class ElementToShow {
    ALL,
    CLUBS,
    EVENTS,
    NONE
}

enum class OrderCriteria {
    SearchResult,
    NameAZ,
    NameZA,
    Distance09
}

enum class LocationType {
    All,
    Outdoor,
    Indoor,
}


data class FilterCriteria(
    var elementToShow: ElementToShow,
    var orderCriteria: OrderCriteria,

    ) : Serializable {
    constructor() : this(
        ElementToShow.ALL,
        OrderCriteria.SearchResult,
    )

    var query: String? = null
    var priceRange = Pair(0, Int.MAX_VALUE)
    var maxDistance = Int.MAX_VALUE
    var genres = LinkedList<String>()
    var locationType = LocationType.All
}