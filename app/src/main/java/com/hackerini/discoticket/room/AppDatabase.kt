package com.hackerini.discoticket.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hackerini.discoticket.objects.Order
import com.hackerini.discoticket.objects.OrderDao
import com.hackerini.discoticket.objects.OrderItem
import com.hackerini.discoticket.objects.OrderItemDao

@Database(entities = [FavoriteClub::class, Order::class, OrderItem::class], version = 6)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteClubDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
}
