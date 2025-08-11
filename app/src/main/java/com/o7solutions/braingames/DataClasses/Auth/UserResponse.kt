package com.o7solutions.braingames.DataClasses.Auth

data class UserResponse(
    val status: Int,
    val success: Boolean,
    val message: String,
    val data: UserData
) {


    data class UserData(
        val _id: String,
        val autoId: Int,
        var streak: Streak,
        val name: String,
        val email: String,
        val password: String,
        val otp: Int,
        val level: Int,
        val playTime: Int,
        val totalGames: Int,
        val totalScore: Int,
        val totalWins: Int,
        val winRate: Int,
        val winStreak: Int,
        val xp: Int,
        val isDelete: Boolean,
        val isBlocked: Boolean,
        val addedById: String?,
        val updatedById: String?,
        val updatedAt: String?,
        val status: Boolean,
        val recentLogins: List<RecentLogin>,
        val createdAt: String,
        val gameHistory: List<GameHistory>
    )

    data class GameHistory(
        val _id: String,
        var bestScore: Int,
        val gameId: String,
        var scoreHistory: MutableList<ScoreHistory>
    )

    data class ScoreHistory(
        val _id: String,
        val date: String,
        val score: Int
    )

    data class Streak(
        val count: Int,
        val timeStamp: String
    )

    data class RecentLogin(
        val userAgent: String,
        val ip: String,
        val _id: String,
        val loginTime: String
    )
}
