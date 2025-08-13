package com.o7solutions.braingames.DataClasses

data class OtpVerification(
    val `data`: Data,
    val message: String,
    val playerId: String,
    val status: Int,
    val success: Boolean,
) {


}
