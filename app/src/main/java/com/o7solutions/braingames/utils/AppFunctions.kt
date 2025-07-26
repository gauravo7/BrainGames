package com.o7solutions.braingames.utils

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.o7solutions.braingames.BottomNav.BottomNavActivity
import com.o7solutions.braingames.DataClasses.Users
import com.o7solutions.braingames.R
import kotlin.random.Random

object AppFunctions
{

    fun showAlert(message: String, context: Context) {

        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Issue")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

            .create()

        alertDialog.show()


    }

    fun showMessage(title: String,message: String, context: Context) {

        val alertDialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

            .create()

        alertDialog.show()


    }
//    fun hideBottomBar(v) {
//        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
//        bottomNav.visibility = View.GONE
//    }iew: View

    fun returnRandom(min: Int,max: Int): Int {
        val randomNumber = Random.nextInt(min,max)
        return randomNumber
    }

    fun getUserDataFromFirestore(userId: String, callback: (Users?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(AppConstants.user).document(userId)

        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val user = document.toObject(Users::class.java)
                callback(user)
            } else {
                callback(null)
            }
        }.addOnFailureListener { e ->
            e.printStackTrace()
            callback(null)
        }
    }

    fun updateUserData(score: Int, win: Boolean,time: Long) {
        val auth = FirebaseAuth.getInstance()
        var userData = Users()


        getUserDataFromFirestore(auth.currentUser?.email.toString()) { user ->
            userData = user!!

            val totalScore = userData.totalScore?.plus(score)
            val wins = userData.totalWins ?: 0
            val totalGames = userData.totalGames ?: 0
            val newTotalGames = totalGames + 1
            val newWins = if (win) wins + 1 else wins
            var newPlayTime = user.playTime?.plus(time)

            val winRate = if (newTotalGames > 0) {
                (newWins.toDouble() / newTotalGames.toDouble() * 100).toInt()
            } else {
                0
            }

            val newUser = Users(
                userData.name,
                userData.email,
                winRate,
                if (win) (userData.winStreak ?: 0) + 1 else 0,
                totalScore,
                newPlayTime,
                newTotalGames,
                newWins,
                0,
                0,
                ArrayList()
            )

            FirebaseFirestore.getInstance()
                .collection(AppConstants.user)
                .document(auth.currentUser?.email.toString())
                .set(newUser)
                .addOnSuccessListener {
                    Log.d("User data", "user data updated successfully")
                }
        }
    }


}