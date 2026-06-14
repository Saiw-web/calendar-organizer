package com.example.myapplication.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.data.local.dao.CalendarDao
import com.example.myapplication.data.local.dao.EventDao
import com.example.myapplication.data.local.dao.PlanDao
import com.example.myapplication.data.local.dao.ProfileDao
import com.example.myapplication.data.local.entity.CalendarEntity
import com.example.myapplication.data.local.entity.EventEntity
import com.example.myapplication.data.local.entity.PlanEntity
import com.example.myapplication.data.local.entity.ProfileEntity

@Database(
    entities = [
        EventEntity::class,
        PlanEntity::class,
        ProfileEntity::class,
        CalendarEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun planDao(): PlanDao
    abstract fun profileDao(): ProfileDao
    abstract fun calendarDao(): CalendarDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "organizer_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
