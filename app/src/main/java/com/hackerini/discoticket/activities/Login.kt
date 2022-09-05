package com.hackerini.discoticket.activities

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.hackerini.discoticket.R
import java.util.regex.Pattern

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.LoginLoginButton)
        val forgottenPassword = findViewById<TextView>(R.id.LoginForgottenPassword)
        val signUpTextView = findViewById<TextView>(R.id.LoginSignUp)

        val emailTextEdit = findViewById<EditText>(R.id.LoginMailAddress)
        val passwordTextEdit = findViewById<EditText>(R.id.LoginPassword)

        forgottenPassword.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        signUpTextView.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        val EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )

        loginButton.setOnClickListener {
            var error = ""
            if (passwordTextEdit.text.toString().isEmpty())
                error += "Password non inserita\n"
            if (emailTextEdit.text.toString().isEmpty())
                error += "E-mail non inserita\n"
            else if (!EMAIL_ADDRESS_PATTERN.matcher(emailTextEdit.text.toString().trim()).matches())
                error += "E-mail non valida\n"

            if (error.isNotEmpty()) {
                val alert = AlertDialog.Builder(this).create()
                alert.setCancelable(false)
                alert.setTitle("Dati inseriti errati")
                alert.setMessage(error)
                alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok") { dialog, _ -> dialog.dismiss() }
                alert.show()
            } else {
                //Form validated
            }
        }

        signUpTextView.setOnClickListener {
            val intent = Intent(applicationContext, SignUp::class.java)
            startActivity(intent)
        }
    }
}