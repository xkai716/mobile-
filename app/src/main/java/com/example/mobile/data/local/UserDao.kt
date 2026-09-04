package com.example.mobile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = 1")
    fun getUser(): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("UPDATE users SET points = :points WHERE id = 1")
    suspend fun updatePoints(points: Int)

    @Query("UPDATE users SET points = MAX(points + :change, 0) WHERE id = 1")
    suspend fun changePoints(change: Int)

    @Query("UPDATE users SET name = :name WHERE id = 1")
    suspend fun updateName(name: String)
}
