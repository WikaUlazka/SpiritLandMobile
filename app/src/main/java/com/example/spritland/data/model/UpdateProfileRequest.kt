package com.example.spritland.data.model

data class UpdateProfileRequest(
    val username: String,
    val favoriteSpiritId: Int?,
    val favoriteAspectId: Int?
)