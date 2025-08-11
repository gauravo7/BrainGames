package com.o7solutions.braingames.Model

import com.o7solutions.braingames.DataClasses.Auth.LoginRequest
import com.o7solutions.braingames.DataClasses.Auth.LoginRequest.LoginResponse
import com.o7solutions.braingames.DataClasses.Auth.RegisterResponse
import com.o7solutions.braingames.DataClasses.Auth.UserResponse
import com.o7solutions.braingames.DataClasses.GameFetchData
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {


    @FormUrlEncoded
    @POST("register")
    fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @POST("login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse>
//    @FormUrlEncoded
//    @POST("login")
//    fun loginUser(
//        @Field("email") email: String,
//        @Field("password") password: String
//    ): Call<UserResponse>

    @POST("game/all")
    suspend fun getGames(@Body body: RequestBody): Response<GameFetchData>

    @POST("player/single")
    @FormUrlEncoded
    suspend fun getUser(
        @Field("_id") id: String
    ): Response<UserResponse>

    @POST("player/update")
    suspend fun updateUser(
        @Body userData: UserResponse.UserData
    ): Response<UserResponse>

    @FormUrlEncoded
    @POST("player/update")
    suspend fun updateScoreUsingForm(
        @Field("_id") userId: String,
        @Field("gameHistory") gameHistory: String // must be a stringified JSON array
    ): Response<ResponseBody>

}