package com.example.mobile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: Int = 1, // Only one user for now
    val name: String,
    val points: Int
)
