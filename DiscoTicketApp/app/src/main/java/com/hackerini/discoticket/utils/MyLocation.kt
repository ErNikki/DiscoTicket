package com.hackerini.discoticket.utils

import android.location.Location

object MyLocation {
    private var location: Location? =null

    fun getLocation():Location?{
        return location
    }

    fun setLocation(location: Location){
        this.location=location
    }

}