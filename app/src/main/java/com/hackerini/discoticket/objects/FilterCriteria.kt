package com.hackerini.discoticket.objects

import android.util.Log
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
    Distance09,
    Price09,
    Price90
}

enum class LocationType {
    //Keep this order
    All,
    Indoor,
    Outdoor,
}


data class FilterCriteria(
    var elementToShow: ElementToShow,
    var orderCriteria: OrderCriteria,

    ) : Serializable {
    constructor() : this(
        ElementToShow.ALL,
        OrderCriteria.SearchResult,
    )

    var query: String = ""
    var priceRange = Pair(0, Int.MAX_VALUE)
    var maxDistance = Int.MAX_VALUE
    var genres = LinkedList<String>()
    var locationType = LocationType.All

    fun filter(elements: LinkedList<Any>): List<Any> {
        //Filter by music
        if (this.genres.isNotEmpty()) {
            elements.retainAll { item ->
                if (item is Club) item.musicGenres.any { s -> this.genres.contains(s) }
                else (item as Event).musicGenres.any { s -> this.genres.contains(s) }
            }
        }

        //Filter by distance
        elements.retainAll { item ->
            if (item is Club) item.distanceFromYou < this.maxDistance
            else (item as Event).club!!.distanceFromYou < this.maxDistance
        }

        //Filter by price range
        elements.retainAll { item ->
            val club = if (item is Club) item else (item as Event).club
            this.priceRange.first < club!!.simpleTicketPrice && club.simpleTicketPrice < this.priceRange.second
        }

        Log.d("FILTER", this.locationType.name)
        //Filter by location type range
        elements.retainAll { item ->
            val club = if (item is Club) item else (item as Event).club

            if (this.locationType == LocationType.Outdoor) {
                club!!.locationType.contains("aperto", ignoreCase = true) ||
                        club.locationType.contains("entrambi", ignoreCase = true)
            } else if (this.locationType == LocationType.Indoor) {
                club!!.locationType.contains("chiuso", ignoreCase = true) ||
                        club.locationType.contains("entrambi", ignoreCase = true)
            } else
                true
        }

        //Sorting createria
        if (this.orderCriteria == OrderCriteria.Distance09) {
            elements.sortBy { item ->
                if (item is Club)
                    item.distanceFromYou
                else
                    (item as Event).club!!.distanceFromYou
            }
        } else if (this.orderCriteria == OrderCriteria.Price09) {
            elements.sortBy { item ->
                if (item is Club)
                    item.simpleTicketPrice
                else
                    (item as Event).club!!.simpleTicketPrice
            }
        } else if (this.orderCriteria == OrderCriteria.Price90) {
            elements.sortByDescending { item ->
                if (item is Club)
                    item.simpleTicketPrice
                else
                    (item as Event).club!!.simpleTicketPrice
            }
        }

        return elements
    }
}