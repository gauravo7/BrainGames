package com.o7solutions.braingames.DataClasses
data class OtpResponse(
    val message: String,
    val otp: Int,
    val status: Int,
    val success: Boolean
)