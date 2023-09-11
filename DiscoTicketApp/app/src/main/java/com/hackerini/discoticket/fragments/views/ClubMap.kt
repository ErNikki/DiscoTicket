package com.hackerini.discoticket.fragments.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.utils.OrderManager
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

enum class TableState {
    Available,
    NotAvailable,
    Selected
}

class Table(var tableId: Int, var rect: RectF, var state: TableState, var numberOfSets: Int)

class ClubMap(context: Context, club: Club, date: String) : View(context) {
    private val linePaint = Paint()
    private val fillPaint = Paint()
    private val textPaint = Paint()

    private val textSize = 50F
    private val cornerRadius = 5
    private val tableWidth = 200F
    private val tableHeigth = 100F

    private val tables = LinkedList<Table>()
    private var tableId = 1
    private var notAvaibleTables = OrderManager.getTablesIds(club,date)

    private var canvas: Canvas? = null
    private var onTableClickListener: ((List<Table>) -> Unit)? = null
    private val selectedTables = LinkedList<Table>()
    var selectableTables = 1
    var isShowMode = false

    private val md = MessageDigest.getInstance("SHA-256")
    private val clubUniqueString = club.name + club.address + club.id + date
    private val hash = md.digest(clubUniqueString.toByteArray(StandardCharsets.UTF_8))
    private val randomStream = hash.inputStream()

    init {
        generateRowOfTable(30F, 30F)
        generateColumnOfTable(30F, 170F)
        generateRowOfTable(30F, 450F)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        this.canvas = canvas

        fillPaint.color = Color.GREEN
        fillPaint.style = Paint.Style.FILL
        linePaint.color = Color.BLACK
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = 10F
        textPaint.color = Color.BLACK
        textPaint.textSize = textSize

        drawAll()
    }

    private fun drawAll() {
        invalidate()
        canvas?.drawRect(RectF(0F, 0F, 1030F, 580F), linePaint)
        tables.forEach { table ->
            drawTable(table)
        }
        canvas?.drawRect(RectF(230F, 160F, 800F, 420F), linePaint)
        canvas?.drawText("Pista", 450F, 300F, textPaint)
        canvas?.drawRect(RectF(830F, 160F, 1000F, 420F), linePaint)
        canvas?.drawText("DJ", 890F, 300F, textPaint)

        drawLegend()

    }

    private fun drawLegend() {
        val legendTextPaint = Paint()
        legendTextPaint.color = Color.BLACK
        legendTextPaint.textSize = 40F

        fillPaint.color = Color.GREEN
        var offset = 0F
        canvas?.drawCircle(30F + offset, 630F, 20F, fillPaint)
        canvas?.drawCircle(30F + offset, 630F, 20F, linePaint)
        canvas?.drawText("Disponibile", 70F + offset, 645F, legendTextPaint)

        fillPaint.color = Color.RED
        offset = 300F
        canvas?.drawCircle(30F + offset, 630F, 20F, fillPaint)
        canvas?.drawCircle(30F + offset, 630F, 20F, linePaint)
        canvas?.drawText("Occupato", 70F + offset, 645F, legendTextPaint)

        if (!isShowMode) {
            fillPaint.color = Color.CYAN
            offset = 600F
            canvas?.drawCircle(30F + offset, 630F, 20F, fillPaint)
            canvas?.drawCircle(30F + offset, 630F, 20F, linePaint)
            canvas?.drawText("Selezionato", 70F + offset, 645F, legendTextPaint)
        }
    }

    private fun generateRowOfTable(startX: Float, startY: Float) {
        for (col in 0..4 step 1) {
            val colStep = 200F * col
            val r = RectF(startX + colStep, startY, tableWidth + colStep, startY + tableHeigth)
            /*
            val tableState = if (randomStream.read() % 100 > 30)
                TableState.Available
            else
                TableState.NotAvailable

             */
            val tableState=TableState.Available
            val table = Table(tableId, r, tableState, 8)
            tables.add(table)
            tableId++
        }
    }

    private fun generateColumnOfTable(startX: Float, startY: Float) {
        for (row in 0..1 step 1) {
            val rowStep = 130F * row
            val r = RectF(startX, startY + rowStep, tableWidth, startY + tableHeigth + rowStep)
            /*
            val tableState = if (randomStream.read() % 100 > 30)
                TableState.Available
            else
                TableState.

             */
            val tableState=TableState.Available
            val table = Table(tableId, r, tableState, 8)
            tables.add(table)
            tableId++
        }
    }

    private fun drawTable(table: Table) {
        if (notAvaibleTables.contains(table.tableId)){
            table.state=TableState.NotAvailable
        }
        fillPaint.color = when (table.state) {
            TableState.Available -> Color.GREEN
            TableState.NotAvailable -> Color.RED
            else -> Color.CYAN
        }
        canvas?.drawRoundRect(table.rect, cornerRadius.toFloat(), cornerRadius.toFloat(), fillPaint)
        canvas?.drawRoundRect(table.rect, cornerRadius.toFloat(), cornerRadius.toFloat(), linePaint)
        canvas?.drawText(
            table.numberOfSets.toString(),
            table.rect.left + tableWidth / 2 - textSize / 2,
            table.rect.top + tableHeigth / 2 + textSize / 2,
            textPaint
        )
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isShowMode)
            return false
        if (event?.action == 1) {
            val x = event.x
            val y = event.y
            for (table in tables) {

                val rect = table.rect
                val isTheSelectedTable =
                    x > rect.left && x < rect.right && y > rect.top && y < rect.bottom

                if (selectableTables > 1) {
                    if (isTheSelectedTable && table.state == TableState.Available && selectedTables.size < selectableTables) {
                        table.state = TableState.Selected
                        selectedTables.add(table)
                        onTableClickListener?.let { it(selectedTables) }
                    } else if (isTheSelectedTable && table.state == TableState.Selected) {
                        table.state = TableState.Available
                        selectedTables.remove(table)
                        onTableClickListener?.let { it(selectedTables) }
                    }
                } else {
                    if (isTheSelectedTable && table.state == TableState.NotAvailable) {
                        break
                    } else if (isTheSelectedTable && table.state == TableState.Available) {
                        selectedTables.clear()
                        table.state = TableState.Selected
                        selectedTables.add(table)
                        onTableClickListener?.let { it(selectedTables) }
                    } else if (table.state == TableState.Selected) {
                        table.state = TableState.Available
                    }
                }
            }
            drawAll()
        }
        return true
    }

    fun setOnTableClickListener(f: (List<Table>) -> Unit) {
        this.onTableClickListener = f
    }


}