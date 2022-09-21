package com.hackerini.discoticket.activities

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.hackerini.discoticket.MainActivity
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.User
import com.hackerini.discoticket.room.RoomManager
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

        val loginLayout = findViewById<ConstraintLayout>(R.id.LoginNotLoggedLayout)
        val logoutLayout = findViewById<ConstraintLayout>(R.id.LoginLoggedLayout)

        val welcomeText = findViewById<TextView>(R.id.LoginWelcomeText)
        val pointsRecup = findViewById<TextView>(R.id.LoginPointRecup)
        val logoutButton = findViewById<Button>(R.id.LoginLogoutButton)
        val deleteAccount = findViewById<Button>(R.id.LoginDeleteAccount)
        val errorMessage = findViewById<TextView>(R.id.LoginWrongCredentials)

        if (User.isLogged(this)) {
            val user = User.getLoggedUser(this)
            welcomeText.text = "Benvenuto ${user?.name}"
            pointsRecup.text = "Hai un totale di ${user!!.points} punti"

            logoutButton.setOnClickListener {
                User.logout(this)
                startActivity(Intent(this, MainActivity::class.java))
            }
            deleteAccount.setOnClickListener {
                User.deleteCurrentAccount(this)
                User.logout(this)
                startActivity(Intent(this, MainActivity::class.java))
            }

            loginLayout.visibility = View.GONE
            logoutLayout.visibility = View.VISIBLE
        } else {
            loginLayout.visibility = View.VISIBLE
            logoutLayout.visibility = View.GONE
        }

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
            errorMessage.visibility = View.GONE
            var hasError = false
            if (passwordTextEdit.text.toString().isEmpty()) {
                passwordTextEdit.error = "Password non inserita"
                hasError = true
            }
            if (emailTextEdit.text.toString().isEmpty()) {
                emailTextEdit.error = "E-mail non inserita"
                hasError = true
            } else if (!EMAIL_ADDRESS_PATTERN.matcher(emailTextEdit.text.toString().trim())
                    .matches()
            ) {
                emailTextEdit.error = "E-mail non valida\n"
                hasError = true
            }

            if (!hasError) {
                //Form validated
                val userDao = RoomManager(this).db.userDao()
                val hashedPassword = User.hashPassword(passwordTextEdit.text.toString())
                val queryResult =
                    userDao.getUserByCredential(emailTextEdit.text.toString(), hashedPassword)
                Log.d("RES", queryResult.toString())
                if (queryResult.isNotEmpty()) {
                    //Success
                    val user = queryResult.first()
                    User.setUserLogged(this, user)
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    errorMessage.visibility = View.VISIBLE
                }
            }
        }

        signUpTextView.setOnClickListener {
            val intent = Intent(applicationContext, SignUp::class.java)
            startActivity(intent)
        }
    }
}