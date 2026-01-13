package com.example.spritland.data.api

import com.example.spritland.data.model.LoginRequest
import com.example.spritland.data.model.LoginResponse
import com.example.spritland.data.model.UserDto
import com.example.spritland.data.model.SpiritListItemDto
import com.example.spritland.data.model.SpiritDetailsDto
import com.example.spritland.data.model.ScenarioDto
import com.example.spritland.data.model.AdversaryDto
import com.example.spritland.data.model.UpdateProfileRequest
import com.example.spritland.data.model.RegisterRequest
import com.example.spritland.data.model.CreateGameRequest
import com.example.spritland.data.model.MyGameListItemDto
import com.example.spritland.data.model.UserListItemDto
import com.example.spritland.data.model.GameDetailsDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.PUT
import retrofit2.http.DELETE

interface ApiService {

    @POST("api/Auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("api/Auth/me")
    suspend fun me(): UserDto

    @PUT("api/Auth/update-profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Any

    @GET("api/Spirits")
    suspend fun getSpirits(): List<SpiritListItemDto>

    @GET("api/Spirits/{id}")
    suspend fun getSpiritById(@Path("id") id: Int): SpiritDetailsDto

    @GET("api/Scenarios")
    suspend fun getScenarios(): List<ScenarioDto>

    @GET("api/Scenarios/{id}")
    suspend fun getScenarioById(@Path("id") id: Int): ScenarioDto

    @GET("api/Adversaries")
    suspend fun getAdversaries(): List<AdversaryDto>

    @GET("api/Adversaries/{id}")
    suspend fun getAdversaryById(@Path("id") id: Int): AdversaryDto

    @POST("api/Auth/register")
    suspend fun register(@Body request: RegisterRequest): Any

    @POST("api/Games")
    suspend fun createGame(@Body request: CreateGameRequest): Any

    @GET("api/Games/user")
    suspend fun getMyGames(): List<MyGameListItemDto>

    @GET("api/Users")
    suspend fun getUsers(@retrofit2.http.Query("search") search: String? = null): List<UserListItemDto>

    @GET("api/Games/{id}")
    suspend fun getGame(@Path("id") id: Int): GameDetailsDto

    @DELETE("api/Games/{id}")
    suspend fun deleteGame(@Path("id") id: Int)

}