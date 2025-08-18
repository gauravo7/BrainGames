package com.example.game

import retrofit2.http.GET
import retrofit2.http.Query

interface WordApiService {
    @GET("random-words")
    suspend fun getRandomWords(
        @Query("maxLength") length: Int,
        @Query("count") count: Int,
        @Query("minLength") minLength: Int
    ): List<String>
}