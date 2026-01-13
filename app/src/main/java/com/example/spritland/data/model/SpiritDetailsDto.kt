package com.example.spritland.data.model

data class SpiritDetailsDto(
    val id: Int,
    val name: String,
    val description: String?,
    val complexity: String,
    val imageUrl: String?,
    val aspects: List<AspectDto>
)
