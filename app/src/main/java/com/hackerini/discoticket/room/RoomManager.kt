package com.hackerini.discoticket.room

import android.content.Context
import androidx.room.Room

class RoomManager(context: Context) {
    val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "discoTicketDb"
    )
        .createFromAsset("disco_ticket.db")
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()
}
