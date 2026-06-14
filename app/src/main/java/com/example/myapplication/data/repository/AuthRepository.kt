package com.example.myapplication.data.repository

import com.example.myapplication.data.local.dao.ProfileDao
import com.example.myapplication.data.local.entity.ProfileEntity
import com.example.myapplication.data.remote.api.ApiService
import com.example.myapplication.data.remote.api.RetrofitClient
import com.example.myapplication.data.remote.model.LoginRequest
import com.example.myapplication.data.remote.model.RegisterRequest
import com.example.myapplication.data.remote.model.UserDto
import kotlinx.coroutines.flow.Flow

class AuthRepository(
    private val apiService: ApiService,
    private val profileDao: ProfileDao
) {
    val profile: Flow<ProfileEntity?> = profileDao.getProfile()

    suspend fun login(email: String, password: String): Result<UserDto> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val authResponse = response.body()!!
                RetrofitClient.setToken(authResponse.token)
                profileDao.saveProfile(
                    ProfileEntity(
                        name = authResponse.user.name,
                        email = authResponse.user.email,
                        avatarUrl = authResponse.user.avatarUrl,
                        token = authResponse.token
                    )
                )
                Result.success(authResponse.user)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<UserDto> {
        return try {
            val response = apiService.register(RegisterRequest(name, email, password))
            if (response.isSuccessful) {
                val authResponse = response.body()!!
                RetrofitClient.setToken(authResponse.token)
                profileDao.saveProfile(
                    ProfileEntity(
                        name = authResponse.user.name,
                        email = authResponse.user.email,
                        avatarUrl = authResponse.user.avatarUrl,
                        token = authResponse.token
                    )
                )
                Result.success(authResponse.user)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        RetrofitClient.setToken(null)
        profileDao.clearProfile()
    }

    suspend fun restoreSession(): Boolean {
        val profile = profileDao.getProfileSync()
        return if (profile?.token != null) {
            RetrofitClient.setToken(profile.token)
            true
        } else {
            false
        }
    }
}
