package com.hackerini.discoticket.objects

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.room.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hackerini.discoticket.activities.Login
import com.hackerini.discoticket.room.RoomManager
import com.hackerini.discoticket.utils.CookieManager
import com.hackerini.discoticket.utils.UserManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.cookies.ConstantCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Cookie
import io.ktor.http.parameters
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.io.Serializable
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

@Entity(tableName = "user")
class User : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var name: String = "Luca"
    var surname: String = "Hackerino"
    var email: String = ""
    var password: String = ""
    var imageProfileUrl: String = ""
    var points: Int = 0

    companion object {

        //usata solo nei social
        //inutile
        fun hashPassword(password: String): String {
            val md = MessageDigest.getInstance("SHA-256")
            val hash = md.digest(password.toByteArray(StandardCharsets.UTF_8))
            return Base64.getEncoder().encodeToString(hash)
        }

        fun setUserLogged(context: Context, user: User) {
            val sharedPreferences = context.getSharedPreferences(
                "DiscoTicket",
                AppCompatActivity.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.putInt("userId", user.id)
            editor.apply()
        }

        fun logout(context: Context) {
            UserManager.logout()
            val sharedPreferences = context.getSharedPreferences(
                "DiscoTicket",
                AppCompatActivity.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.remove("userId")
            editor.apply()

        }

        fun getLoggedUser(context: Context): User? {
            val sharedPreferences = context.getSharedPreferences(
                "DiscoTicket",
                AppCompatActivity.MODE_PRIVATE
            )
            val userId = sharedPreferences.getInt("userId", -1)
            return if (userId == -1) null
            else {

                //val userDao = RoomManager(context).db.userDao()
                //userDao.getUserById(userId).firstOrNull()
                return UserManager.getUser()
            }
        }

        //da implementare!
        fun deleteCurrentAccount(context: Context):Boolean {
            val user = UserManager.getUser()
            val flag=UserManager.deleteAccount(user)
            val sharedPreferences = context.getSharedPreferences(
                "DiscoTicket",
                AppCompatActivity.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.remove("userId")
            editor.apply()
            return flag
        }

        fun isLogged(context: Context) : Boolean{
            return UserManager.isUserLogged()
        }


        fun generateNotLoggedAlertDialog(context: Context): AlertDialog {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle("Accesso non effettuato")
            builder.setMessage("Per proseguire è necessario effettuare l'accesso")
            builder.setPositiveButton("Accedi") { _, _ ->
                context.startActivity(Intent(context, Login::class.java))
            }
            builder.setNegativeButton("Annulla") { dialog, _ ->
                dialog.dismiss()
            }
            return builder.create()
        }

        fun isCurrentSocialAccount(context: Context): Boolean {
            val current = getLoggedUser(context)
            if (current == null) return false
            //Because the social account have reserved id, from 1 to 3 (included)
            return current.id <= 3
        }
    }
}

data class UserWithDiscount(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val items: List<Discount>
) : Serializable

@Dao
interface UserDao {
    //inutile
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    //usata da if user exist, usata a sua volta nel login
    //inutile
    @Query("SELECT * FROM user WHERE email=:mail LIMIT 1")
    fun getUserByMail(mail: String): List<User>

    //usata nell'accesso ai socialclub
    @Query("SELECT * FROM user WHERE email=:mail and password=:password LIMIT 1")
    fun getUserByCredential(mail: String, password: String): List<User>

    //inutile
    @Query("SELECT * FROM user WHERE id=:id LIMIT 1")
    fun getUserById(id: Int): List<User>

    @Transaction
    @Query("SELECT * FROM `user` WHERE id=:id")
    fun getUserDiscount(id: Int): List<UserWithDiscount>

    @Query("UPDATE user SET points=:points WHERE id=:id")
    fun updatePoints(points: Int, id: Int)

    @Query("UPDATE user SET points=points+:points WHERE id=:id")
    fun incrementsPoints(points: Int, id: Int)

    //inutile
    fun isUserExists(mail: String): Boolean {
        return getUserByMail(mail).isNotEmpty()
    }

    //inutile
    @Insert
    fun insert(user: User)

    //da fare
    @Delete
    fun delete(user: User)
}