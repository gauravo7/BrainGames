package com.o7solutions.braingames.DataClasses.Auth

data class RegisterResponse(
    val status: Int,
    val token: String,
    val success: Boolean,
    val message: String,
    val data: UserData
)

data class UserData(
    val _id: String,
    val name: String,
    val email: String,
    val xp: Int,
    val level: Int,
    val totalScore: Int,
    val totalWins: Int,
    val winStreak: Int,
    val createdAt: String
)
