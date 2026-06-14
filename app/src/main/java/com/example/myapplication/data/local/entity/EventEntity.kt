package com.example.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = CalendarEntity::class,
            parentColumns = ["id"],
            childColumns = ["calendarId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("calendarId")]
)
data class EventEntity(
    @PrimaryKey
    val id: String,
    val calendarId: String,
    val title: String,
    val description: String? = null,
    val startTime: Long,
    val endTime: Long,
    val location: String? = null,
    val color: Int? = null,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
