package com.o7solutions.braingames.DataClasses

data class Wordresponse(
    val `data`: List<String>,
    val message: String,
    val status: Int,
    val success: Boolean
)