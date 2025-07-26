package com.o7solutions.braingames.Services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.o7solutions.braingames.R
import com.o7solutions.braingames.utils.AppConstants
import com.o7solutions.braingames.utils.AppFunctions

class UserTimerService: Service() {

    private var startTime: Long = 0
    val auth = FirebaseAuth.getInstance()
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval: Long = 1000L
    private var elapsedTime = 0L
    private lateinit var userId: String
    val db = FirebaseFirestore.getInstance()


    private val updateTask = object : Runnable {
        override fun run() {
            elapsedTime = System.currentTimeMillis() - startTime
            handler.postDelayed(this, updateInterval)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTime = System.currentTimeMillis()
        userId = intent?.getStringExtra("userId") ?: "unknown"
        handler.post(updateTask)

        Log.d("User Time Service","Service started")
        startForeground(1, createNotification())
        return START_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacks(updateTask)
        val secondsSpent = elapsedTime / 1000
        sendTimeToFirebase(secondsSpent)
        super.onDestroy()
    }



    private fun sendTimeToFirebase(seconds: Long) {

        var lastTime = 0
        AppFunctions.getUserDataFromFirestore(auth.currentUser?.email.toString()) { user->
            lastTime = user!!.playTime!!.toInt()
        }
        var newTime = lastTime + seconds
        db.collection(AppConstants.user).document(auth.currentUser?.email.toString())
            .update("playTime",newTime)
            .addOnSuccessListener {
                Log.e("User Time Service","Time updated")
            }
            .addOnFailureListener { e->
                Log.e("User Time Service",e.printStackTrace().toString())
            }
    }

    private fun createNotification(): Notification {
        val channelId = "user_timer_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "User Timer Service",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Tracking Gaming time")
            .setSmallIcon(R.drawable.time)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}