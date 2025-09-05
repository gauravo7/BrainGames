package com.o7solutions.braingames.UI

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.trusted.TokenStore
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.o7solutions.braingames.BottomNav.BottomNavActivity
import com.o7solutions.braingames.HomeScreens.HomeScreenActivity
import com.o7solutions.braingames.R
import com.o7solutions.braingames.auth.LoginActivity
import com.o7solutions.braingames.utils.AppFunctions

class SplashActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

//        val animationZoomOut = AnimationUtils.loadAnimation(this, R.anim.rotate)
//        findViewById<ImageView>(R.id.logoImg).startAnimation(animationZoomOut)

        Handler(Looper.getMainLooper()).postDelayed({

            var token = AppFunctions.getToken(this)

            if(token != null) {
                val intent = Intent(this, HomeScreenActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
//            if(auth.currentUser != null) {
//                val intent = Intent(this, BottomNavActivity::class.java)
//                startActivity(intent)
//                finish()
//            } else {
//                val intent = Intent(this, LoginActivity::class.java)
//                startActivity(intent)
//                finish()
//            }

        },2000)

    }
}