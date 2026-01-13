package com.example.spritland.data.model

data class CreateGameRequest(
    val creatorUserId: Int,
    val datePlayed: String, // ISO np. "2026-01-12T00:00:00"
    val adversaryId: Int?,
    val scenarioId: Int?,
    val difficulty: Int?,
    val boardSetup: String?,
    val result: String,
    val endReason: String?,
    val turns: Int?,
    val comment: String?,
    val blightedIsland: Boolean,
    val players: List<CreateGamePlayerRequest>
)

data class CreateGamePlayerRequest(
    val userId: Int,
    val spiritId: Int?,
    val aspectId: Int?,
    val notes: String?
)