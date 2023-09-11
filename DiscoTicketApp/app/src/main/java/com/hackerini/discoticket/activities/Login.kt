package com.hackerini.discoticket.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hackerini.discoticket.MainActivity
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.User
import com.hackerini.discoticket.objects.UserDao
import com.hackerini.discoticket.room.RoomManager
import com.hackerini.discoticket.utils.CookieManager
import com.hackerini.discoticket.utils.UserManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.cookies.ConstantCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Cookie
import io.ktor.http.parameters
import io.ktor.http.setCookie
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.util.*
import java.util.regex.Pattern
import kotlin.concurrent.schedule


class Login : AppCompatActivity(), TextWatcher {

    lateinit var loginWithGoogle: CardView
    lateinit var loginWithFacebook: CardView
    lateinit var loginWithApple: CardView
    lateinit var userDao: UserDao
    private lateinit var passwordLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        userDao = RoomManager(this).db.userDao()

        val loginButton = findViewById<Button>(R.id.LoginLoginButton)
        val forgottenPassword = findViewById<TextView>(R.id.LoginForgottenPassword)
        val signUpTextView = findViewById<TextView>(R.id.LoginSignUp)
        val reSendConfEmail = findViewById<TextView>(R.id.loginReSendConfEmail)

        val emailTextEdit = findViewById<TextInputEditText>(R.id.LoginMailAddress)
        val passwordTextEdit = findViewById<TextInputEditText>(R.id.LoginPassword)
        passwordLayout = findViewById(R.id.LoginPasswordLayout)
        passwordLayout.endIconMode = TextInputLayout.END_ICON_NONE

        val loginLayout = findViewById<ConstraintLayout>(R.id.LoginNotLoggedLayout)
        val logoutLayout = findViewById<ConstraintLayout>(R.id.LoginLoggedLayout)

        val welcomeText = findViewById<TextView>(R.id.LoginWelcomeText)
        val pointsRecup = findViewById<TextView>(R.id.LoginPointRecup)
        val logoutButton = findViewById<Button>(R.id.LoginLogoutButton)
        val deleteAccount = findViewById<Button>(R.id.LoginDeleteAccount)
        val errorMessage = findViewById<TextView>(R.id.LoginWrongCredentials)

        loginWithGoogle = findViewById(R.id.LoginCardLoginWithGoogle)
        loginWithFacebook = findViewById(R.id.LoginCardLoginWithFacebook)
        loginWithApple = findViewById(R.id.LoginCardLoginWithApple)

        loginWithGoogle.setOnClickListener { onSocialLoginClick(it) }
        loginWithFacebook.setOnClickListener { onSocialLoginClick(it) }
        loginWithApple.setOnClickListener { onSocialLoginClick(it) }

        if (User.isLogged(this)) {
            val user = User.getLoggedUser(this)
            welcomeText.text = "Benvenuto ${user?.name}"
            pointsRecup.text = "Hai un totale di ${user!!.points} punti"

            logoutButton.setOnClickListener {
                User.logout(this)
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Logout avvenuto con successo", Toast.LENGTH_LONG).show()
            }
            deleteAccount.setOnClickListener {
                val alertDialog = MaterialAlertDialogBuilder(this)
                alertDialog.setTitle("Conferma eliminazione account")
                alertDialog.setMessage("Sei sicuro di voler eliminare il tuo account?\nL'operazione è irreversibile!")
                alertDialog.setPositiveButton("Conferma") { dialog, _ ->
                    if (!User.isCurrentSocialAccount(this)) //Since social accounts are fake, the shouldn't be deleted
                        User.deleteCurrentAccount(this)
                    User.logout(this)
                    startActivity(Intent(this, MainActivity::class.java))
                    Toast.makeText(this, "Account eliminato con successo", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
                alertDialog.setNegativeButton("Annulla") { dialog, _ ->
                    dialog.dismiss()
                }
                alertDialog.create().show()
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

                val (result,message) = UserManager.login(emailTextEdit.text.toString(), passwordTextEdit.text.toString())
                if (result) {
                    actionsAfterLogin(UserManager.getUser())

                }
                else{
                    showErrorDialog(message)
                    errorMessage.visibility = View.VISIBLE
                }





            }
        }

        passwordTextEdit.addTextChangedListener(this)

        signUpTextView.setOnClickListener {
            val intent = Intent(applicationContext, SignUp::class.java)
            startActivity(intent)
        }
        forgottenPassword.setOnClickListener {
            val intent = Intent(applicationContext, ForgotPassword::class.java)
            startActivity(intent)
        }
        reSendConfEmail.setOnClickListener {
            val intent = Intent(applicationContext, ReSendConfirmationEmail::class.java)
            startActivity(intent)
        }
    }

    private fun onSocialLoginClick(view: View) {
        val hashedPassword = User.hashPassword("password")
        val loggedUser =
            when (view.id) {
                loginWithGoogle.id -> userDao.getUserByCredential(
                    "utente@google.com",
                    hashedPassword
                )
                loginWithFacebook.id -> userDao.getUserByCredential(
                    "utente@facebook.com",
                    hashedPassword
                )
                loginWithApple.id -> userDao.getUserByCredential("utente@apple.com", hashedPassword)
                else -> listOf()
            }
        if (loggedUser.isEmpty()) {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Errore")
            builder.setMessage("Impossibile accedere con questo account, creare un account utilizzando la propria e-mail")
            builder.setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
            builder.create().show()
        } else {
            val builder = MaterialAlertDialogBuilder(this)
            val inflater: LayoutInflater = this.layoutInflater
            builder.setView(inflater.inflate(R.layout.loading_dialog, null))
            builder.setCancelable(true)
            val dialog = builder.create()
            dialog.show()
            Timer().schedule(1000) {
                dialog.dismiss()
                runOnUiThread {
                    actionsAfterLogin(loggedUser.first())
                }
            }
        }

    }

    fun actionsAfterLogin(user: User) {
        Toast.makeText(this, "Accesso effettuato con successo", Toast.LENGTH_LONG)
            .show()
        User.setUserLogged(this, user)
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s != null && s.isNotEmpty())
            passwordLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
        else
            passwordLayout.endIconMode = TextInputLayout.END_ICON_NONE
    }

    override fun afterTextChanged(s: Editable?) {
    }

    private fun showErrorDialog(message : String ){
        val dialog = AlertDialog.Builder(this).create()
        dialog.setTitle("Errore")
        dialog.setMessage("C'è stato un problema con la registrazione: \n" + message)
        dialog.setCancelable(false)
        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            "Login"
        ) { d, _ ->
            d.dismiss()
        }
        dialog.show()
    }
}
