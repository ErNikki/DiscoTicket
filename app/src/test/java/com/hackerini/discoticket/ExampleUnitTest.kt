package com.hackerini.discoticket

import com.hackerini.discoticket.objects.OrderPreview
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun OrderPreviewIsCorrect() {
        val orderPreview = OrderPreview()
        assertEquals(orderPreview.getTotalAmount(), 65.5F)
    }
}