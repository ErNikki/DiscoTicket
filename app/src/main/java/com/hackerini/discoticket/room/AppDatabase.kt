package com.hackerini.discoticket.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteClub::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteClubDao
}
