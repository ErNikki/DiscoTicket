package com.hackerini.discoticket

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hackerini.discoticket.objects.ItemType
import com.hackerini.discoticket.objects.Order
import com.hackerini.discoticket.objects.OrderItem
import com.hackerini.discoticket.room.RoomManager
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var roomManager: RoomManager


    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        roomManager = RoomManager(context)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        roomManager.db.close()
    }

    @Test
    @Throws(Exception::class)
    fun makeAnOrder() {
        val order = Order()
        roomManager.db.orderDao().insert(order)
        val insertedId = roomManager.db.orderDao().getLastId()
        val orderItem0 = OrderItem(0, "A", 1, 10F, ItemType.Ticket, insertedId)
        val orderItem1 = OrderItem(0, "B", 2, 15F, ItemType.Ticket, insertedId)
        roomManager.db.orderItemDao().insert(orderItem0)
        roomManager.db.orderItemDao().insert(orderItem1)

        val orderRecup = roomManager.db.orderDao().getOrderWithOrderItem(insertedId)
        Assert.assertEquals(orderRecup.getTotalAmount(), 40F)
    }

}