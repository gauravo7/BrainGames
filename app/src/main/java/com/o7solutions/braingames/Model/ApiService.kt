package com.o7solutions.braingames.Model

import com.o7solutions.braingames.DataClasses.Auth.LoginRequest
import com.o7solutions.braingames.DataClasses.Auth.LoginRequest.LoginResponse
import com.o7solutions.braingames.DataClasses.Auth.RegisterResponse
import com.o7solutions.braingames.DataClasses.Auth.UserResponse
import com.o7solutions.braingames.DataClasses.GameFetchData
import com.o7solutions.braingames.DataClasses.OtpResponse
import com.o7solutions.braingames.DataClasses.OtpVerification
import com.o7solutions.braingames.DataClasses.Wordresponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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


    @FormUrlEncoded
    @POST("player/update")
    suspend fun updateTips(
        @Field("_id") userId: String,
        @Field("tips") tips: Int // must be a stringified JSON array
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("sendOtp")
    suspend fun requestOTP(
        @Field("email") email: String,
    ): Response<OtpResponse>

    @FormUrlEncoded
    @POST("verifyOtp")
    suspend fun verifyOTP(
        @Field("email") email: String,
        @Field("otp") otp: Int,
    ): Response<OtpVerification>

    @FormUrlEncoded
    @POST("changeForgottenPassword")
    suspend fun changePassword(
        @Field("_id") id: String,
        @Field("password") password: String
    ): Response<ResponseBody>


    @GET("random-words")
    suspend fun getRandomWords(
        @Query("maxLength") length: Int,
        @Query("count") count: Int,
        @Query("minLength") minLength: Int
    ): Response<Wordresponse>
}