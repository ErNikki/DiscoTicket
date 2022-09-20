package com.hackerini.discoticket.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hackerini.discoticket.objects.*

@Database(
    entities = [FavoriteClub::class, Order::class, OrderItem::class, User::class],
    version = 10
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteClubDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun userDao(): UserDao
}
