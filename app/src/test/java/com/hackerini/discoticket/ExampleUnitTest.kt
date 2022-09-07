package com.hackerini.discoticket

import com.hackerini.discoticket.objects.OrderItem
import com.hackerini.discoticket.objects.OrderPreview
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun OrderPreviewIsCorrect() {
        val orderPreview = OrderPreview()
        orderPreview.drinks.add(OrderItem("a", 2, 5.5F))
        orderPreview.drinks.add(OrderItem("a", 1, 7.5F))
        orderPreview.tickets.add(OrderItem("a", 3, 5F))

        assertEquals(orderPreview.getTotalAmount(), 33.5F)
    }
}