package com.o7solutions.braingames.DataClasses

import java.net.URL

data class Games(
    var id: Long ?= null,
    var name: String ?= null,
    var fragmentId: String ?= null,
    var url: String ?= null,
    var colorHex: String ?= null,
    var version: Int ?= 0
)
