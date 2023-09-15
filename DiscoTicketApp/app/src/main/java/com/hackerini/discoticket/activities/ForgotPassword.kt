package com.hackerini.discoticket.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputLayout
import com.hackerini.discoticket.R
import com.hackerini.discoticket.utils.CookieManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.HttpResponse
import io.ktor.http.parameters
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.util.regex.Pattern

class ForgotPassword : AppCompatActivity() {
    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    lateinit var email: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        email = findViewById(R.id.SignUpEMail)

        val button = findViewById<Button>(R.id.forgotPasswordButton)

        button.setOnClickListener{
            val error=hasError()
            if(!error){
                runBlocking {
                    val client = HttpClient()
                    val response: HttpResponse = client.submitForm(
                        url = CookieManager.url+"AccountsManager/forgotPassword",
                        formParameters = parameters {
                            append("email", email.text.toString())

                        }
                    )
                    client.close()
                    val jsonObj = Json.parseToJsonElement(response.body())
                        .jsonObject
                        .toMap()


                    if (jsonObj.get("success")?.toString().equals("true")) {
                        showSuccessDialog()
                    }
                    else{
                        showErrorDialog(jsonObj.get("message").toString())
                    }
                }
            }
        }
    }

    private fun showErrorDialog(message : String ){
        val dialog = AlertDialog.Builder(this).create()
        dialog.setTitle("Errore")
        dialog.setMessage(message)
        dialog.setCancelable(false)
        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            "try again"
        ) { d, _ ->
            d.dismiss()
        }
        dialog.show()
    }

    private fun showSuccessDialog() {
        val dialog = AlertDialog.Builder(this).create()
        dialog.setTitle("Email Inviata")
        dialog.setMessage("Se l'email è presente nel nostro sistema riceverai un'email con il link per aggiornare la tua password.")
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

        email.error = null

        if (email.text.isBlank()) {
            email.error = "Inserisci l'email"
            hasError = true
        }

        if (!EMAIL_ADDRESS_PATTERN.matcher(email.text.toString().trim()).matches()) {
            email.error = "Email non valida"
            hasError = true
        }

        return hasError
    }
}