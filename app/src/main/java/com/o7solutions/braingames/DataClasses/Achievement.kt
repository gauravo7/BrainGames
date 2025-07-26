package com.o7solutions.braingames.DataClasses
import java.io.Serializable

data class Achievement(
    var id: Long ?= 0,
    var title: String ?= null,
    var description: String ?= null
): Serializable
