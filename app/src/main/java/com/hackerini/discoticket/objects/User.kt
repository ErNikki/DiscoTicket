package com.hackerini.discoticket.objects

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.room.*
import com.hackerini.discoticket.room.RoomManager
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

    companion object {

        fun hashPassword(password: String): String {
            val md = MessageDigest.getInstance("SHA-256")
            val hash = md.digest(password.toByteArray(StandardCharsets.UTF_8))
            return Base64.getEncoder().encodeToString(hash)
        }

        fun setUserLogged(context: Context, user: User) {
            val sharedPreferences = context.getSharedPreferences(
                "DiscoTicketPref",
                AppCompatActivity.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.putInt("userId", user.id)
            editor.apply()
        }

        fun logout(context: Context) {
            val sharedPreferences = context.getSharedPreferences(
                "DiscoTicketPref",
                AppCompatActivity.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.remove("userId")
            editor.apply()
        }

        fun getLoggedUser(context: Context): User? {
            val sharedPreferences = context.getSharedPreferences(
                "DiscoTicketPref",
                AppCompatActivity.MODE_PRIVATE
            )
            val userId = sharedPreferences.getInt("userId", -1)
            return if (userId == -1) null
            else {
                val userDao = RoomManager(context).db.userDao()
                userDao.getUserById(userId).firstOrNull()
            }
        }

        fun deleteCurrentAccount(context: Context) {
            val user = getLoggedUser(context)
            user?.let {
                val userDao = RoomManager(context).db.userDao()
                userDao.delete(it)
            }
        }

        fun isLogged(context: Context): Boolean {
            val sharedPreferences = context.getSharedPreferences(
                "DiscoTicketPref",
                AppCompatActivity.MODE_PRIVATE
            )
            val userId = sharedPreferences.getInt("userId", -1)
            return userId != -1
        }
    }
}

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE email=:mail LIMIT 1")
    fun getUserByMail(mail: String): List<User>

    @Query("SELECT * FROM user WHERE email=:mail and password=:password LIMIT 1")
    fun getUserByCredential(mail: String, password: String): List<User>

    @Query("SELECT * FROM user WHERE id=:id LIMIT 1")
    fun getUserById(id: Int): List<User>

    fun isUserExists(mail: String): Boolean {
        return getUserByMail(mail).isNotEmpty()
    }

    @Insert
    fun insert(user: User)

    @Delete
    fun delete(user: User)
}