package com.example.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.data.local.entity.CalendarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarDao {
    @Query("SELECT * FROM calendars")
    fun getAllCalendars(): Flow<List<CalendarEntity>>

    @Query("SELECT * FROM calendars WHERE id = :id")
    suspend fun getCalendarById(id: String): CalendarEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendar(calendar: CalendarEntity)

    @Query("DELETE FROM calendars WHERE id = :id")
    suspend fun deleteCalendar(id: String)
}
