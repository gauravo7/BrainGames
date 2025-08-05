package com.o7solutions.braingames.utils

import android.app.Application
import com.o7solutions.braingames.Model.RetrofitClient

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.setToken(applicationContext)
    }
}