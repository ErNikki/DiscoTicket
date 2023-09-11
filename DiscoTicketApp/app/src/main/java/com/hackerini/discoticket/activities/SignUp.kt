package com.hackerini.discoticket.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.User
import com.hackerini.discoticket.objects.UserDao
import com.hackerini.discoticket.room.RoomManager
import com.hackerini.discoticket.utils.UserManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.HttpResponse
import io.ktor.http.parameters
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
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
    lateinit var passwordLayout: TextInputLayout
    lateinit var passwordRepeatLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        userDao = RoomManager(this).db.userDao()


        name = findViewById(R.id.SignUpName)
        surname = findViewById(R.id.SignUpSurname)
        email = findViewById(R.id.SignUpEMail)
        password = findViewById(R.id.SignUpPassword)
        passwordRepeat = findViewById(R.id.SignUpPasswordRepeat)
        passwordLayout = findViewById(R.id.SignUpPasswordLayout)
        passwordRepeatLayout = findViewById(R.id.SignUpPasswordRepeatLayout)
        passwordLayout.endIconMode = TextInputLayout.END_ICON_NONE
        passwordRepeatLayout.endIconMode = TextInputLayout.END_ICON_NONE

        val button = findViewById<Button>(R.id.SignUpButton)

        button.setOnClickListener {
            val error = hasError()
            //Log.d("okok","ihi")
            if (!error) {
                //val user = User()
                val name = name.text.toString()
                val surname = surname.text.toString()
                val email = email.text.toString()
                val password = password.text.toString()


                //userDao.insert(user)

                val (result,message)=UserManager.signUp(email,email,password,name,surname)
                /*
                val editor = getSharedPreferences(
                    "DiscoTicket",
                    MODE_PRIVATE
                ).edit()
                editor.remove("userId")
                editor.apply()
                */
                if (result) {
                    showSuccessDialog()
                }
                else{
                    showErrorDialog(message)
                }




            }
        }

        password.addTextChangedListener(object : TextWatcher {
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

        })

        passwordRepeat.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.isNotEmpty())
                    passwordRepeatLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                else
                    passwordRepeatLayout.endIconMode = TextInputLayout.END_ICON_NONE
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

    }

    private fun showSuccessDialog() {
        val dialog = AlertDialog.Builder(this).create()
        dialog.setTitle("Registrazione completata")
        dialog.setMessage("Registrazione avvenuta con successo!\nPrima di accedere devi confermare l'account dal link che ti abbiamo inviato sull'email.")
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
            //It must be called 2 times
            passwordRepeatLayout.endIconMode = TextInputLayout.END_ICON_NONE
            passwordRepeatLayout.endIconMode = TextInputLayout.END_ICON_NONE
            passwordRepeat.error = "Le due password non coincidono"
            hasError = true
        }
        if (!EMAIL_ADDRESS_PATTERN.matcher(email.text.toString().trim()).matches()) {
            email.error = "Email non valida"
            hasError = true
        }
        /*
        if (userDao.isUserExists(email.text.toString())) {
            email.error = "Email già esistente"
            hasError = true
        }*/


        return hasError
    }
}