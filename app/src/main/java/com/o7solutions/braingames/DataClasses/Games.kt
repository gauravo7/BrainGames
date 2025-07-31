package com.o7solutions.braingames.DataClasses

import java.net.URL

data class Games(
    var id: Long ?= null,
    var name: String ?= null,
    var fragmentId: String ?= null,
    var url: String ?= null,
    var colorHex: String ?= null,
    var version: Int ?= 0,
    var category: Int ?= 0,
    var time: Int ?= 0,
    var timeAdded: Int ?= 0,
    var positiveScore: String ?= null,
    var negativeScore: String ?= null,
)
