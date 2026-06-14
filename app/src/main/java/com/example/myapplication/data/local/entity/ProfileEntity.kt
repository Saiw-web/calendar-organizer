package com.example.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey
    val id: String = "default",
    val name: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val token: String? = null
)
