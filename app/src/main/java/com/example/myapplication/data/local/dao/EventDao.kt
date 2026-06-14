package com.example.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events WHERE calendarId = :calendarId ORDER BY startTime ASC")
    fun getEventsByCalendar(calendarId: String): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE calendarId = :calendarId AND startTime >= :startOfDay AND startTime < :endOfDay ORDER BY startTime ASC")
    fun getEventsForDay(calendarId: String, startOfDay: Long, endOfDay: Long): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: String): EventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteEventById(id: String)

    @Query("SELECT * FROM events WHERE isSynced = 0")
    suspend fun getUnsyncedEvents(): List<EventEntity>
}
