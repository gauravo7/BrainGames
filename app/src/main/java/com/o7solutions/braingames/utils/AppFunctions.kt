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
import com.o7solutions.braingames.DataClasses.BestScore
import com.o7solutions.braingames.DataClasses.Streak
import com.o7solutions.braingames.DataClasses.Users
import com.o7solutions.braingames.R
import com.o7solutions.braingames.R.layout.dialog_result
import kotlin.random.Random

object AppFunctions {

    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()
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

    fun showMessage(title: String, message: String, context: Context) {

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

    fun returnRandom(min: Int, max: Int): Int {
        val randomNumber = Random.nextInt(min, max)
        return randomNumber
    }

//    fun updateBestScore() {
//
//        db.collection()
//
//    }

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

    fun updateUserData(score: Int, win: Boolean, time: Long,id: Int) {
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

        updateBestScore(id,score)
    }

    fun getStreak(callback: (Streak) -> Unit) {
        val email = auth.currentUser?.email.toString()
        db.collection(AppConstants.streak).document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val streak = document.toObject(Streak::class.java)
                    callback(streak ?: Streak())
                } else {
                    callback(Streak())
                }
            }
            .addOnFailureListener {
                callback(Streak())
            }
    }


    fun updateBestScore(gameId: Int, newScore: Int) {
        val email = auth.currentUser?.email.toString()
        val docRef = db.collection(AppConstants.games)
            .document(gameId.toString())
            .collection(gameId.toString())
            .document(email)

        docRef.get().addOnSuccessListener { document ->
            val currentBest = if (document.exists()) {
                document.toObject(BestScore::class.java)?.bestScore ?: 0
            } else {
                0
            }

            if (newScore > currentBest) {
                val updatedScore = BestScore(email = email, bestScore = newScore)
                docRef.set(updatedScore)
            }
        }.addOnFailureListener {
            Log.e("UpdateBestScore", "Error getting document", it)
        }
    }


    fun getBestScore(id: Int, callback: (Int?) -> Unit) {
        val email = auth.currentUser?.email.toString()

        db.collection(AppConstants.games)
            .document(id.toString())
            .collection(id.toString())
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val bestScore = document.toObject(BestScore::class.java)?.bestScore
                    callback(bestScore)
                } else {
                    callback(0) // or null, if you prefer
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }


    fun updateDailyStreak() {
        val email = auth.currentUser?.email.toString()
        val streakRef = db.collection(AppConstants.streak).document(email)

        streakRef.get().addOnSuccessListener { document ->
            val currentTime = System.currentTimeMillis()
            val oneDayMillis = 24 * 60 * 60 * 1000L

            if (document.exists()) {
                val streak = document.toObject(Streak::class.java)
                val lastTime = streak?.timestamp ?: 0
                val diff = currentTime - lastTime

                val newStreak = when {
                    diff < oneDayMillis -> streak?.count ?: 1 // Already updated today
                    diff < 2 * oneDayMillis -> (streak?.count ?: 0) + 1 // Daily streak continues
                    else -> 1 // Reset streak
                }

                val updatedStreak = Streak(count = newStreak, timestamp = currentTime)
                streakRef.set(updatedStreak)
            } else {
                val newStreak = Streak(count = 1, timestamp = currentTime)
                streakRef.set(newStreak)
            }
        }.addOnFailureListener {
        }
    }


    fun showResultDialog(userAnswer: String, correctAnswer: String, context: Context) {
    val dialogView = View.inflate(context, R.layout.dialog_result, null)

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

    fun saveToken(context: Context, token: String) {
        val sharedPref = context.getSharedPreferences(AppConstants.userPref, Context.MODE_PRIVATE)
        sharedPref.edit().putString(AppConstants.token, token).apply()
    }

    fun getToken(context: Context): String? {
        val sharedPref = context.getSharedPreferences(AppConstants.userPref, Context.MODE_PRIVATE)
        return sharedPref.getString(AppConstants.token, null)
    }

    fun deleteToken(context: Context) {
        val sharedPref = context.getSharedPreferences(AppConstants.userPref, Context.MODE_PRIVATE)
        sharedPref.edit().remove(AppConstants.token).apply()
    }

    fun deleteUserId(context: Context) {
        val sharedPref = context.getSharedPreferences(AppConstants.userPref, Context.MODE_PRIVATE)
        sharedPref.edit().remove(AppConstants.userId).apply()
    }

    fun saveUserId(context: Context, userId: String) {
        val sharedPref = context.getSharedPreferences(AppConstants.userPref, Context.MODE_PRIVATE)
        sharedPref.edit().putString(AppConstants.userId, userId).apply()
    }

    fun getUserId(context: Context): String? {
        val sharedPref = context.getSharedPreferences(AppConstants.userPref, Context.MODE_PRIVATE)
        return sharedPref.getString(AppConstants.userId, null)
    }

    fun isTokenAvailable(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(AppConstants.userPref, Context.MODE_PRIVATE)
        return sharedPref.contains(AppConstants.token)
    }


}