package com.example.game

object GameResult {
    var finalScore: Int? = null
    var allLevelsCompleted: Boolean? = null
    var timePlayedMillis: Long? = null

    fun clear() {
        finalScore = null
        allLevelsCompleted = null
        timePlayedMillis = null
    }
}