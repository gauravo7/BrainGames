package com.o7solutions.braingames.Model

import com.o7solutions.braingames.DataClasses.Auth.LoginRequest
import com.o7solutions.braingames.DataClasses.Auth.UserData
import com.o7solutions.braingames.DataClasses.Auth.UserResponse
import com.o7solutions.braingames.DataClasses.GameFetchData
import com.o7solutions.braingames.utils.AppFunctions
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class Repository(val api: ApiService) {

    suspend fun fetchGames(): StateClass<List<GameFetchData.Data>> {
        return try {
            val emptyJson = "{}".toRequestBody("application/json".toMediaType())
//            val response = apiService.getAllGames(emptyJson)

            val response = api.getGames(emptyJson)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                StateClass.Success(body.data)
            } else {
                StateClass.Error(response.message())
            }
        } catch (e: Exception) {
            StateClass.Error(e.localizedMessage ?: "Unknown Error")
        }
    }


    suspend fun getUserData(userID: String): StateClass<UserResponse.UserData> {
        return try {
            val response = api.getUser(userID)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                StateClass.Success(body.data)
            } else {
                StateClass.Error(response.message())
            }
        } catch (e: Exception) {
            StateClass.Error(e.localizedMessage ?: "Unknown Error")
        }
    }

}