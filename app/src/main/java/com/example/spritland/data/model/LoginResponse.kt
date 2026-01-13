package com.example.spritland.data.model

data class LoginResponse(
    val token: String,
    val user: UserDto
)