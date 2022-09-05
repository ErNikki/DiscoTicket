package com.hackerini.discoticket.room

import androidx.room.*


@Entity(tableName = "favorite")
data class FavoriteClub (
    @PrimaryKey
    val id: Int
)

@Dao
interface FavoriteClubDao {
    @Query("SELECT * FROM favorite")
    fun getAll(): List<FavoriteClub>

    @Insert
    fun insertFavorite(id: FavoriteClub)

    @Delete
    fun deleteFavorite(id: FavoriteClub)
}
