package com.o7solutions.braingames.Model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import android.content.Context
import com.o7solutions.braingames.utils.AppConstants
import com.o7solutions.braingames.utils.AppFunctions
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitClient {
    private const val BASE_URL = "https://e1b5fda72c57.ngrok-free.app/api/"

    // Token (stored once available)
    private var token: String? = null

    fun setToken(context: Context) {
//        val sharedPref = context.getSharedPreferences(AppConstants.userPref, Context.MODE_PRIVATE)
//        token = sharedPref.getString(AppConstants.token, null)
        token = AppFunctions.getToken(context)
    }

    // Authenticated client with token
    private val authInterceptor = Interceptor { chain ->
        val original: Request = chain.request()
        val requestBuilder = original.newBuilder()
        requestBuilder.header("Authorization", "$token")
//        requestBuilder.header("Authorization", AppFunctions.getToken(context = ))
        val request = requestBuilder.build()
        chain.proceed(request)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Most detailed
    }
    private val authClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val defaultClient = OkHttpClient.Builder().build()

    // Retrofit without token (login/signup)
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(defaultClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // Retrofit with token (after login/signup)
    val authInstance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(authClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
