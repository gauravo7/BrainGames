package com.o7solutions.braingames.DataClasses

import java.util.logging.Level

data class Users(
    var name: String ?= null,
    var email: String ?= null,
    var winRate: Int ?= 0,
    var winStreak: Int ?= 0,
    var totalScore: Int ?= 0,
    var playTime: Float ?= 0.0f,
    var totalGames: Int ?= 0,
//    var globalRank: Int ?= 0,
    var totalWins: Int ?= 0,
    var xp: Int ?= 0,
    var level: Int ?= 0,
    var achievements: ArrayList<Achievement>

) {
    constructor(): this("","",0,0,0,0.0f,0,0,0,0, ArrayList())
}