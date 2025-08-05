package com.o7solutions.braingames.DataClasses

data class GameFetchData(
    val `data`: List<Data>,
    val message: String,
    val status: Int,
    val success: Boolean,
    val total: Int
) {

    data class Data(
        val __v: Int,
        val _id: String,
        val addedById: String,
        val autoId: Int,
        val colorHex: String,
        val createdAt: String,
        val fragmentId: String,
        val gameCategoryId: String,
        val image: String,
        val initialTime: Int,
        val isBlocked: Boolean,
        val isDelete: Boolean,
        val name: String,
        val negativeScore: Int,
        val positiveScore: Int,
        val status: Boolean,
        val timeAdded: Int,
        val totalGames: Int,
        val updatedAt: Any,
        val updatedById: Any,
        val version: Int
    )
}