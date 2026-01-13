package com.example.spritland.data.model

data class GameDetailsDto(
    val id: Int,
    val creatorUserId: Int,
    val datePlayed: String,
    val adversaryId: Int?,
    val scenarioId: Int?,
    val difficulty: Int?,
    val boardSetup: String?,
    val result: String,
    val endReason: String?,
    val turns: Int?,
    val comment: String?,
    val blightedIsland: Boolean,
    val players: List<GamePlayerDto>
)

data class GamePlayerDto(
    val id: Int,
    val userId: Int,
    val spiritId: Int?,
    val aspectId: Int?,
    val notes: String?
)
