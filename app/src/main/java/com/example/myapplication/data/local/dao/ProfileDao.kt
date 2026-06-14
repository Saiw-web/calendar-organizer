package com.example.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile WHERE id = 'default'")
    fun getProfile(): Flow<ProfileEntity?>

    @Query("SELECT * FROM profile WHERE id = 'default'")
    suspend fun getProfileSync(): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: ProfileEntity)

    @Query("DELETE FROM profile")
    suspend fun clearProfile()
}
