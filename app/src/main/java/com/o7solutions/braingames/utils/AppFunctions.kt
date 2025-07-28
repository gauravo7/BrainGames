package com.o7solutions.braingames.utils

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.o7solutions.braingames.BottomNav.BottomNavActivity
import com.o7solutions.braingames.DataClasses.Users
import com.o7solutions.braingames.R
import com.o7solutions.braingames.R.layout.dialog_result
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

     fun showResultDialog(userAnswer: String, correctAnswer: String,context: Context) {
        val dialogView = View.inflate(context,R.layout.dialog_result, null)

        val wrongText = dialogView.findViewById<TextView>(R.id.wrongAnswerText)
        val correctText = dialogView.findViewById<TextView>(R.id.correctAnswerText)
        val okBtn = dialogView.findViewById<Button>(R.id.okButton)

        wrongText.text = "Your Answer: $userAnswer❌"
        correctText.text = "Correct Answer: $correctAnswer✅"

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        okBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

//    private fun showCustomDialog(message: String,context: Context,points: Int) {
//        val dialogView = View.inflate(context,R.layout.time_finish, null)
//
//        var winingMessage = ""
//        if(points > 200) {
//            winingMessage = "You won \uD83D\uDE00 "
//        } else {
//            winingMessage = "You lose \uD83D\uDE22"
//        }
//
//        val animation = AnimationUtils.loadAnimation(context, R.anim.fade)
//        val titleView = dialogView.findViewById<TextView>(R.id.dialogTitle)
//        val messageView = dialogView.findViewById<TextView>(R.id.dialogMessage)
//        val okButton = dialogView.findViewById<Button>(R.id.okButton)
//
//        titleView.startAnimation(animation)
//        titleView.text = "\u23F3 Time Up"
////        messageView.text ="$rightQuestions/$totalQuestions \n$message\n$winingMessage"
//
//        val dialog = AlertDialog.Builder(context)
//            .setView(dialogView)
//            .setCancelable(false)
//            .create()
//
//        okButton.setOnClickListener {
//            if(points < 200) {
//                AppFunctions.updateUserData(points,false,60000)
//            } else {
//                AppFunctions.updateUserData(points,true,60000)
//            }
//            dialog.dismiss()
//            findNavController().popBackStack()
//        }
//
//        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//        dialog.show()
//    }


}