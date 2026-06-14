package com.example.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendars")
data class CalendarEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val ownerEmail: String,
    val description: String? = null,
    val color: Int? = null
)
