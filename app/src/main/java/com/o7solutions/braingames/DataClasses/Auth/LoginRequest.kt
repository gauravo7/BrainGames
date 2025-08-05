package com.o7solutions.braingames.DataClasses.Auth

data class LoginRequest(
    val email: String,
    val password: String
) {


    data class RecentLogin(
        val userAgent: String,
        val ip: String,
        val loginTime: String
    )

    data class LoginData(

        val _id: String,
        val autoId: Int,
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
        val recentLogins: List<UserResponse.RecentLogin>,
        val createdAt: String,
        val gameHistory: List<Any>
    )


    data class LoginResponse(
        val token: String,
        val success: Boolean,
        val message: String,
        val data: LoginData
    )
}