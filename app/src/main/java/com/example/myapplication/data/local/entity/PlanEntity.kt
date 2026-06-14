package com.example.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plans")
data class PlanEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String? = null,
    val dueDate: Long? = null,
    val priority: String = "medium",
    val status: String = "active",
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
