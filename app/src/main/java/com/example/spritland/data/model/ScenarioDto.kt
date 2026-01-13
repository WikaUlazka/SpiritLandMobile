package com.example.spritland.data.model

data class ScenarioDto(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val difficulty: String,
    val createdAt: String,
    val updatedAt: String
)