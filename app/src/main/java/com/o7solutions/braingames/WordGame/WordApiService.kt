package com.example.game

import retrofit2.http.GET
import retrofit2.http.Query

interface WordApiService {
    @GET("word")
    suspend fun getRandomWords(
        @Query("length") length: Int,
        @Query("number") count: Int
    ): List<String>
}