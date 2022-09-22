package com.hackerini.discoticket.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.User
import com.hackerini.discoticket.objects.UserDao
import com.hackerini.discoticket.room.RoomManager
import java.util.regex.Pattern

class SignUp : AppCompatActivity() {

    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    lateinit var name: EditText
    lateinit var surname: EditText
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var passwordRepeat: EditText
    lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        userDao = RoomManager(this).db.userDao()


        name = findViewById<EditText>(R.id.SignUpName)
        surname = findViewById<EditText>(R.id.SignUpSurname)
        email = findViewById<EditText>(R.id.SignUpEMail)
        password = findViewById<EditText>(R.id.SignUpPassword)
        passwordRepeat = findViewById<EditText>(R.id.SignUpPasswordRepeat)
        val button = findViewById<Button>(R.id.SignUpButton)

        button.setOnClickListener {
            val error = hasError()
            if (!error) {
                val user = User()
                user.name = name.text.toString()
                user.surname = surname.text.toString()
                user.email = email.text.toString()
                user.password = User.hashPassword(password.text.toString())
                userDao.insert(user)
                val editor = getSharedPreferences(
                    "DiscoTicketPref",
                    AppCompatActivity.MODE_PRIVATE
                ).edit()
                editor.remove("userId")
                editor.apply()
                showSuccessDialog()

            }
        }

    }

    private fun showSuccessDialog() {
        val dialog = AlertDialog.Builder(this).create()
        dialog.setTitle("Registrazione completata")
        dialog.setMessage("Registrazione avvenuta con successo\nOra puoi effettuare il login con il tuo nuovo account")
        dialog.setCancelable(false)
        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            "Login"
        ) { d, _ ->
            d.dismiss()
            startActivity(Intent(this, Login::class.java))
        }
        dialog.show()
    }

    private fun hasError(): Boolean {
        var hasError = false

        name.error = null
        surname.error = null
        email.error = null
        password.error = null
        passwordRepeat.error = null

        if (name.text.isBlank()) {
            name.error = "Inserisci il nome"
            hasError = true
        }
        if (surname.text.isBlank()) {
            surname.error = "Inserisci il cognome"
            hasError = true
        }
        if (email.text.isBlank()) {
            email.error = "Inserisci l'email"
            hasError = true
        }
        if (password.text.isBlank()) {
            password.error = "Inserisci password"
            hasError = true
        }
        if (passwordRepeat.text.isBlank()) {
            passwordRepeat.error = "Ripeti password"
            hasError = true
        }
        if (password.text.length < 6) {
            password.error = "Inserisci almeno 6 caratteri"
            hasError = true
        }
        if (password.text.toString() != passwordRepeat.text.toString()) {
            passwordRepeat.error = "La password deve essere la stessa inserite prima"
            hasError = true
        }
        if (!EMAIL_ADDRESS_PATTERN.matcher(email.text.toString().trim()).matches()) {
            email.error = "Indirizzo non valido"
            hasError = true
        }
        if (userDao.isUserExists(email.text.toString())) {
            email.error = "Indirizzo già esistente"
            hasError = true
        }


        return hasError
    }
}