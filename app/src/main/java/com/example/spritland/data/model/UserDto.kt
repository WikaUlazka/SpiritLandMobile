package com.example.spritland.data.model

data class UserDto(
    val id: Int,
    val username: String,
    val email: String,
    val createdAt: String,
    val favoriteSpiritId: Int?,
    val favoriteAspectId: Int?
)

