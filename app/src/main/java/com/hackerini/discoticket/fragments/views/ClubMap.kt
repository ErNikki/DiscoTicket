package com.hackerini.discoticket.fragments.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View


class ClubMap(context: Context) : View(context) {
    val linePaint = Paint()
    val fillPaint = Paint()
    val textPaint = Paint()

    val textSize = 50F
    val cornerRadius = 5

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)


        fillPaint.color = Color.GREEN
        fillPaint.style = Paint.Style.FILL
        linePaint.color = Color.BLACK
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = 10F
        textPaint.color = Color.BLACK
        textPaint.textSize = textSize

        drawRow(canvas, 30F, 30F)
        drawRow(canvas, 30F, 450F)

    }

    fun drawRow(canvas: Canvas?, startX: Float, startY: Float) {
        for (col in 0..4 step 1) {
            val tableWidth = 200F
            val tableHeigth = 100F
            val colStep = 200F * col
            val r = RectF(startX + colStep, startY, tableWidth + colStep, startY + tableHeigth)
            canvas?.drawRoundRect(r, cornerRadius.toFloat(), cornerRadius.toFloat(), fillPaint)
            canvas?.drawRoundRect(r, cornerRadius.toFloat(), cornerRadius.toFloat(), linePaint)
            canvas?.drawText(
                col.toString(),
                30F + colStep + tableWidth / 2 - textSize / 2,
                startY + tableHeigth/2 + textSize / 2,
                textPaint
            )
        }
    }
}