package com.hackerini.discoticket.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hackerini.discoticket.objects.*

@Database(
    entities = [FavoriteClub::class, User::class, Discount::class],
    version = 19
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteClubDao
    abstract fun userDao(): UserDao
    abstract fun discountDao(): DiscountDao
}
