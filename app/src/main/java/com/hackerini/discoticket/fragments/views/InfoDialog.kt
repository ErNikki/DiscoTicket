package com.hackerini.discoticket.fragments.views

import android.content.Context
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import java.math.BigInteger
import java.security.MessageDigest

class InfoDialog(val title: String, val description: String, val context: Context) {
    private val checkbox = CheckBox(context)
    private val linearLayout = LinearLayout(context)
    private val descriptionTextView = TextView(context)

    init {
        descriptionTextView.text = description
        checkbox.text = "Non mostrare più questo messaggio"

        linearLayout.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val paddingDp = 16
        val density = context.resources.displayMetrics.density
        val paddingPixel = (paddingDp * density).toInt()
        linearLayout.orientation = LinearLayout.VERTICAL
        descriptionTextView.setPadding(
            paddingPixel,
            paddingPixel / 2,
            paddingPixel,
            paddingPixel / 2
        )
        linearLayout.addView(descriptionTextView)
        linearLayout.addView(checkbox)
    }

    private fun closeDialog() {
        if (checkbox?.isChecked == true) {
            val sharedPreferences = context.getSharedPreferences(
                "DiscoTicketDialogSetting",
                Context.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.putBoolean(this.getID(), false)
            editor.commit()
        }
    }

    fun shouldBeOpened(): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            "DiscoTicketDialogSetting",
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(this.getID(), true)
    }

    fun show() {

        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setView(linearLayout)
        builder.setPositiveButton("Ok") { dialog, _ ->
            run {
                closeDialog()
                dialog.dismiss()
            }
        }
        builder.create().show()
    }

    private fun getID(): String {
        val digest = MessageDigest.getInstance("MD5")
        digest.update(this.description.toByteArray())
        val messageDigest = digest.digest()
        val number = BigInteger(1, messageDigest)
        return number.toString(16)
    }

}