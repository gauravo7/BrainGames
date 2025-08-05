package com.o7solutions.braingames.Model

sealed class StateClass<out T> {
    data class Success<out T>(val data: T): StateClass<T>()
    data class Error(val message: String): StateClass<Nothing>()
    object Loading : StateClass<Nothing>()
}
