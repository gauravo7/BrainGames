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
        val name: String,
        val email: String,
        val xp: Int,
        val totalScore: Int,
        val recentLogins: List<RecentLogin>
    )

    data class LoginResponse(
        val token: String,
        val success: Boolean,
        val message: String,
        val data: LoginData
    )
}