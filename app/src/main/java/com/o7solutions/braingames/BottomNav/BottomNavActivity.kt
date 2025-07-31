package com.o7solutions.braingames.BottomNav

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.o7solutions.braingames.R
import com.o7solutions.braingames.databinding.ActivityBottomNavBinding
import com.o7solutions.braingames.utils.AppFunctions

class BottomNavActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNavBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBottomNavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_bottom_nav)
        AppFunctions.updateDailyStreak()

//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.homeFragment
//            )
//        )
//        supportActionBar?.setBackgroundDrawable(
//            ColorDrawable(ContextCompat.getColor(this, R.color.primary))
//        )
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//
//
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
    override fun onSupportNavigateUp(): Boolean {
        return  super.onSupportNavigateUp()|| navController.popBackStack()
    }

    fun showBottomNav(show: Boolean) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNav.visibility = if (show) View.VISIBLE else View.GONE
    }

}