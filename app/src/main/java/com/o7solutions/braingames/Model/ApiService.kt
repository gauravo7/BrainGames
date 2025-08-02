package com.o7solutions.braingames.Model

import com.o7solutions.braingames.DataClasses.Auth.LoginRequest
import com.o7solutions.braingames.DataClasses.Auth.LoginRequest.LoginResponse
import com.o7solutions.braingames.DataClasses.Auth.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
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
}