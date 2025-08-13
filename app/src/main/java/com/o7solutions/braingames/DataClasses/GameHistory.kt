package com.o7solutions.braingames.DataClasses

data class GameHistory(
    val _id: String,
    val bestScore: Int,
    val gameId: String,
    val scoreHistory: List<ScoreHistory>
)