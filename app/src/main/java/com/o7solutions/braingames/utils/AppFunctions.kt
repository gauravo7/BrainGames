package com.o7solutions.braingames.utils

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
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
import com.google.gson.Gson
import com.o7solutions.braingames.BottomNav.BottomNavActivity
import com.o7solutions.braingames.DataClasses.Auth.UserResponse
import com.o7solutions.braingames.DataClasses.Auth.UserResponse.GameHistory
import com.o7solutions.braingames.DataClasses.BestScore
import com.o7solutions.braingames.DataClasses.Streak
import com.o7solutions.braingames.DataClasses.Users
import com.o7solutions.braingames.Model.ApiService
import com.o7solutions.braingames.Model.RetrofitClient
import com.o7solutions.braingames.R
import com.o7solutions.braingames.R.layout.dialog_result
import com.o7solutions.braingames.utils.AppConstants.KEY_TIPS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

object AppFunctions {

    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()

    fun getCurrentISODateUTC(): String {
        val instant = java.time.Instant.now()
        return instant.toString() // Format: 2025-08-07T08:02:03.406Z
    }


//    fun updateUserDataThroughApi(
//        score: Int,
//        win: Boolean,
//        time: Long,
//        id: String,
//        context: Context
//    ) {
//        CoroutineScope(Dispatchers.Main).launch {
//            val response = RetrofitClient.authInstance.getUser(getUserId(context).toString())
//            if (response.isSuccessful && response.body() != null) {
//
//                val userData = response.body()!!.data
//
//                val games = if (userData.totalGames > 0) userData.totalGames else 1
//                val winRate = ((userData.totalWins.toDouble() / games.toDouble()) * 100).toInt()
//                val playTime = userData.playTime.toLong() + time
//                val winStreak = if (win) userData.winStreak + 1 else 0
//
//                // Create new ScoreHistory object
//                val newScoreHistory = UserResponse.ScoreHistory(
//                    _id = "", // backend can assign if required
//                    date = getCurrentISODateUTC(), // Get current ISO date
//                    score = score
//                )
//
//                // Map and update gameHistory with new scoreHistory
//                val updatedGameHistory = userData.gameHistory.map { game ->
//                    if (game.gameId == id) {
//                        val updatedScoreList = game.scoreHistory.toMutableList()
//                        updatedScoreList.add(newScoreHistory)
//
//                        val updatedBestScore = if (score > game.bestScore) score else game.bestScore
//
//                        game.copy(
//                            bestScore = updatedBestScore,
//                            scoreHistory = updatedScoreList
//                        )
//                    } else {
//                        game
//                    }
//                }.toMutableList()
//
//                // If gameId not found, add new game entry
//                if (userData.gameHistory.none { it.gameId == id }) {
//                    val newGame = UserResponse.GameHistory(
//                        _id = "",
//                        gameId = id,
//                        bestScore = score,
//                        scoreHistory = mutableListOf(newScoreHistory)
//                    )
//                    updatedGameHistory.add(newGame)
//                }
//
//                val updatedUser = userData.copy(
//                    totalScore = userData.totalScore + score,
//                    winRate = winRate,
//                    playTime = playTime.toInt(),
//                    winStreak = winStreak,
//                    totalGames = userData.totalGames + 1,
//                    totalWins = if (win) userData.totalWins + 1 else userData.totalWins,
//                    gameHistory = updatedGameHistory
//                )
//
//                updateUserData(updatedUser, context)
//            }
//        }
//    }
//
//    fun updateUserDataThroughApi(
//        score: Int,
//        win: Boolean,
//        time: Long,
//        id: String,
//        context: Context
//    ) {
//        CoroutineScope(Dispatchers.Main).launch {
//            val response = RetrofitClient.authInstance.getUser(getUserId(context).toString())
//            if (response.isSuccessful && response.body() != null) {
//
//                val userData = response.body()!!.data
//
//                val games = if (userData.totalGames > 0) userData.totalGames else 1
//                val winRate = ((userData.totalWins.toDouble() / games.toDouble()) * 100).toInt()
//                val playTime = userData.playTime.toLong() + time
//                val winStreak = if (win) userData.winStreak + 1 else 0
//
//                // Deep copy of gameHistory
//                val gameHistoryList = userData.gameHistory.toMutableList()
//
//                // Try to find existing game
//                val existingGame = gameHistoryList.find { it.gameId == id }
//
//                if (existingGame != null) {
//                    // Create a mutable copy of scoreHistory
//                    val updatedScoreHistory = existingGame.scoreHistory.toMutableList()
//
//                    // Add new score entry
//                    updatedScoreHistory.add(
//                        UserResponse.ScoreHistory(
//                            _id = "", // Fill this with server-generated id if needed
//                            date = getCurrentISODateUTC().toString(),
//                            score = score
//                        )
//                    )
//
//                    // Update bestScore if needed
//                    val updatedBestScore = maxOf(existingGame.bestScore, score)
//
//                    // Replace the GameHistory entry
//                    val updatedGame = existingGame.copy(
//                        bestScore = updatedBestScore,
//                        scoreHistory = updatedScoreHistory
//                    )
//
//                    val index = gameHistoryList.indexOfFirst { it.gameId == id }
//                    if (index != -1) {
//                        gameHistoryList[index] = updatedGame
//                    }
//
//                } else {
//                    // Game does not exist, create new one
//                    val newGame = UserResponse.GameHistory(
//                        _id = "",
//                        bestScore = score,
//                        gameId = id,
//                        scoreHistory = mutableListOf(
//                            UserResponse.ScoreHistory(
//                                _id = "",
//                                date = getCurrentISODateUTC().toString(),
//                                score = score
//                            )
//                        )
//                    )
//                    gameHistoryList.add(newGame)
//                }
//
//                // Update user data
//                val updatedUser = userData.copy(
//                    totalScore = userData.totalScore + score,
//                    winRate = winRate,
//                    playTime = playTime.toInt(),
//                    winStreak = winStreak,
//                    totalGames = userData.totalGames + 1,
//                    totalWins = if (win) userData.totalWins + 1 else userData.totalWins,
//                    gameHistory = gameHistoryList
//                )
//
//                updateUserData(updatedUser, context)
//            }
//        }
//    }

    fun updateUserDataThroughApi(
        score: Int,
        win: Boolean,
        time: Long,
        id: String,
        context: Context
    ) {
        Log.d("UpdateUser", "Function called")

        CoroutineScope(Dispatchers.Main).launch {
            val userId = getUserId(context).toString()
            Log.d("UpdateUser", "Fetching user with ID: $userId")

            val response = RetrofitClient.authInstance.getUser(userId)
            if (response.isSuccessful && response.body() != null) {

                val userData = response.body()!!.data as UserResponse.UserData
                Log.d("UpdateUser", "User data fetched successfully")

                val games = if (userData.totalGames > 0) userData.totalGames else 1
                val winRate = ((userData.totalWins.toDouble() / games.toDouble()) * 100).toInt()
                val playTime = userData.playTime + time.toInt()
                val winStreak = if (win) userData.winStreak + 1 else 0

                val updatedGameHistory = userData.gameHistory.toMutableList()

                val index = updatedGameHistory.indexOfFirst { it.gameId == id }

                if (index != -1) {
                    Log.d("UpdateUser", "Existing game found for gameId = $id")

                    val existingGame = updatedGameHistory[index]
                    val updatedScoreHistory = existingGame.scoreHistory.toMutableList()

                    Log.d("UpdateUser", "Previous scoreHistory size: ${updatedScoreHistory.size}")

                    updatedScoreHistory.add(
                        UserResponse.ScoreHistory(
                            _id = "",
                            date = getCurrentISODateUTC().toString(),
                            score = score
                        )
                    )

                    Log.d("UpdateUser", "New score added. Updated size: ${updatedScoreHistory.size}")

                    val updatedGame = existingGame.copy(
                        bestScore = maxOf(existingGame.bestScore, score),
                        scoreHistory = updatedScoreHistory
                    )

                    updatedGameHistory[index] = updatedGame

                    Log.d("UpdateUser", "GameHistory updated at index $index")
                } else {
                    Log.d("UpdateUser", "No existing game found. Creating new gameHistory for gameId = $id")

                    val newScoreHistory = mutableListOf(
                        UserResponse.ScoreHistory(
                            _id = "",
                            date = getCurrentISODateUTC().toString(),
                            score = score
                        )
                    )

                    updatedGameHistory.add(
                        UserResponse.GameHistory(
                            _id = "",
                            gameId = id,
                            bestScore = score,
                            scoreHistory = newScoreHistory
                        )
                    )

                    Log.d("UpdateUser", "New GameHistory added. Total games: ${updatedGameHistory.size}")
                }

                val updatedUser = userData.copy(
                    totalScore = userData.totalScore + score,
                    winRate = winRate,
                    playTime = playTime,
                    winStreak = winStreak,
                    totalGames = userData.totalGames + 1,
                    totalWins = if (win) userData.totalWins + 1 else userData.totalWins,
                    gameHistory = updatedGameHistory
                )

                Log.d("UpdateUser", "Updating user with updatedGameHistory size: ${updatedGameHistory.size}")
                updateScoreUsingFormEncoded(context,getUserId(context).toString(),id,score)
                updateUserData(updatedUser, context)

            } else {
                Log.e("UpdateUser", "API call failed or response is null")
            }
        }
    }

    interface UpdateUserCallback {
        fun onSuccess()
        fun onError(message: String)
    }

    fun updateUserDataThroughApi(
        score: Int,
        win: Boolean,
        time: Long,
        id: String,
        context: Context,
        callback: UpdateUserCallback
    ) {
        Log.d("UpdateUser", "Function called")

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val userId = getUserId(context).toString()
                Log.d("UpdateUser", "Fetching user with ID: $userId")

                val response = RetrofitClient.authInstance.getUser(userId)
                if (response.isSuccessful && response.body() != null) {

                    val userData = response.body()!!.data as UserResponse.UserData
                    Log.d("UpdateUser", "User data fetched successfully")

                    val games = if (userData.totalGames > 0) userData.totalGames else 1
                    val winRate = ((userData.totalWins.toDouble() / games.toDouble()) * 100).toInt()
                    val playTime = userData.playTime + time.toInt()
                    val winStreak = if (win) userData.winStreak + 1 else 0

                    val updatedGameHistory = userData.gameHistory.toMutableList()
                    val index = updatedGameHistory.indexOfFirst { it.gameId == id }

                    if (index != -1) {
                        val existingGame = updatedGameHistory[index]
                        val updatedScoreHistory = existingGame.scoreHistory.toMutableList()

                        updatedScoreHistory.add(
                            UserResponse.ScoreHistory(
                                _id = "",
                                date = getCurrentISODateUTC().toString(),
                                score = score
                            )
                        )

                        val updatedGame = existingGame.copy(
                            bestScore = maxOf(existingGame.bestScore, score),
                            scoreHistory = updatedScoreHistory
                        )

                        updatedGameHistory[index] = updatedGame
                    } else {
                        val newScoreHistory = mutableListOf(
                            UserResponse.ScoreHistory(
                                _id = "",
                                date = getCurrentISODateUTC().toString(),
                                score = score
                            )
                        )

                        updatedGameHistory.add(
                            UserResponse.GameHistory(
                                _id = "",
                                gameId = id,
                                bestScore = score,
                                scoreHistory = newScoreHistory
                            )
                        )
                    }

                    val updatedUser = userData.copy(
                        totalScore = userData.totalScore + score,
                        winRate = winRate,
                        playTime = playTime,
                        winStreak = winStreak,
                        totalGames = userData.totalGames + 1,
                        totalWins = if (win) userData.totalWins + 1 else userData.totalWins,
                        gameHistory = updatedGameHistory
                    )

                    updateScoreUsingFormEncoded(context, userId, id, score)
                    updateUserData(updatedUser, context)

                    Log.d("UpdateUser", "Update successful ✅")
                    callback.onSuccess()

                } else {
                    Log.e("UpdateUser", "API call failed or response is null ❌")
                    callback.onError("API call failed or response is null")
                }
            } catch (e: Exception) {
                Log.e("UpdateUser", "Exception: ${e.message}", e)
                callback.onError(e.message ?: "Unknown error")
            }
        }
    }


//    fun updateUserDataThroughApi(
//        score: Int,
//        win: Boolean,
//        time: Long,
//        id: String,
//        context: Context
//    ) {
//            CoroutineScope(Dispatchers.Main).launch {
//                val response = RetrofitClient.authInstance.getUser(getUserId(context).toString())
//                if (response.isSuccessful && response.body() != null) {
//
//                    val userData = response.body()!!.data as UserResponse.UserData
//
//                    var games = if(userData.totalGames> 0) userData.totalGames else 1
//                    var winRate = ((userData.totalWins.toDouble() / games.toDouble()) * 100).toInt()
//                    var playTime = userData.playTime.toLong() + time
//                    var winStreak = if (win) userData.winStreak + 1 else 0
//
//                    var list = arrayListOf<UserResponse.GameHistory>()
//                    var scoreHistoryList = arrayListOf<UserResponse.ScoreHistory>()
//
//                    list.addAll(userData.gameHistory)
//
//                    var item = list.find { it.gameId == id }
//
//                    userData.gameHistory.find { it.gameId == id }?.apply {
//                        scoreHistoryList.addAll(scoreHistory)
//                    }
//
//
//                    scoreHistoryList.add(UserResponse.ScoreHistory("",getCurrentISODateUTC().toString(),score))
//                    if (item != null) {
//                        // Update score if higher
////                        if (item.bestScore < score) {
////                            item.bestScore = score
////                            list.add(UserResponse.GameHistory(gameId = id, bestScore = score,_id= "", scoreHistory = scoreHistoryList))
//
////                        }
//                    } else {
//                        // Add new game entry if not found
//                        list.add(UserResponse.GameHistory(gameId = id, bestScore = score,_id= "", scoreHistory = scoreHistoryList))
//                    }
//
////                      updating score history
//                    list.find {it.gameId == id  }?.apply {
//                        scoreHistory.addAll(scoreHistoryList)
//                    }
//
////                    list.add(UserResponse.GameHistory(gameId = id, bestScore = score,_id= "", scoreHistory = scoreHistoryList))
//
//
//
//                    val user = response.body()!!.data.copy(
//                        totalScore = userData.totalScore + score,  // change the score
//                        winRate = winRate,
//                        playTime = playTime.toInt(),
//                        winStreak = winStreak,// change win rate
//                        totalGames = userData.totalGames + 1,
//                        totalWins = if (win) userData.totalWins + 1 else userData.totalWins,
//                        gameHistory = list
//                    )
//
//                    updateUserData(user,context)
////                updateUserData(user)
//                }
//            }
//        }

//
suspend fun updateUserData(user: UserResponse.UserData, context: Context) {
    try {
        val response = RetrofitClient.authInstance.updateUser(user)

        if (response.isSuccessful && response.body() != null) {
            val updatedUserData = response.body()!!.data

            saveUser(context, updatedUserData)

            Log.d("User data updated", getUser(context).toString())
            Log.d("Score history", updatedUserData.gameHistory.toString())
        } else {
            Log.e("UpdateUser", "Error updating user: ${response.message()}")
        }
    } catch (e: Exception) {
        Log.e("UpdateUser", "Exception: ${e.localizedMessage}")
    }
}

    fun updateScoreUsingFormEncoded(
        context: Context,
        userId: String,
        gameId: String,
        score: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val gameHistoryJson = JSONArray().apply {
                    put(JSONObject().apply {
                        put("gameId", gameId)
                        put("score", score)
                    })
                }.toString()

                val response = RetrofitClient.authInstance.updateScoreUsingForm(
                    userId,
                    gameHistoryJson
                )

                if (response.isSuccessful) {
                    Log.d("UpdateScore", "Score updated successfully")
                } else {
                    Log.e("UpdateScore", "Failed: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("UpdateScore", "Exception: ${e.localizedMessage}")
            }
        }
    }


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

    fun fetchUserData(userId: String, context: Context){

        var point =0
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.authInstance.getUser(userId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val userData = response.body()!!.data as UserResponse.UserData
                        saveUser(context, userData)
                        println("User Name: ${userData.name}")
                        println("Total Wins: ${userData.totalWins}")
                        println("Game History: ${userData.gameHistory}")
                    } else {
                        println("Error: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    println("Exception: ${e.localizedMessage}")
                }
            }
        }
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

//    fun updateUserData(score: Int, win: Boolean, time: Long, id: Int) {
//        val auth = FirebaseAuth.getInstance()
//        var userData = Users()
//
//
//        getUserDataFromFirestore(auth.currentUser?.email.toString()) { user ->
//            userData = user!!
//
//            val totalScore = userData.totalScore?.plus(score)
//            val wins = userData.totalWins ?: 0
//            val totalGames = userData.totalGames ?: 0
//            val newTotalGames = totalGames + 1
//            val newWins = if (win) wins + 1 else wins
//            var newPlayTime = user.playTime?.plus(time)
//
//            val winRate = if (newTotalGames > 0) {
//                (newWins.toDouble() / newTotalGames.toDouble() * 100).toInt()
//            } else {
//                0
//            }
//
//            val newUser = Users(
//                userData.name,
//                userData.email,
//                winRate,
//                if (win) (userData.winStreak ?: 0) + 1 else 0,
//                totalScore,
//                newPlayTime,
//                newTotalGames,
//                newWins,
//                0,
//                0,
//                ArrayList()
//            )
//
//            FirebaseFirestore.getInstance()
//                .collection(AppConstants.user)
//                .document(auth.currentUser?.email.toString())
//                .set(newUser)
//                .addOnSuccessListener {
//                    Log.d("User data", "user data updated successfully")
//                }
//        }
//
////        updateBestScore(id, score)
//    }

//    fun getStreak(callback: (Streak) -> Unit) {
//        val email = auth.currentUser?.email.toString()
//        db.collection(AppConstants.streak).document(email)
//            .get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    val streak = document.toObject(Streak::class.java)
//                    callback(streak ?: Streak())
//                } else {
//                    callback(Streak())
//                }
//            }
//            .addOnFailureListener {
//                callback(Streak())
//            }
//    }


//    fun updateBestScore(gameId: Int, newScore: Int) {
//        val email = auth.currentUser?.email.toString()
//        val docRef = db.collection(AppConstants.games)
//            .document(gameId.toString())
//            .collection(gameId.toString())
//            .document(email)
//
//        docRef.get().addOnSuccessListener { document ->
//            val currentBest = if (document.exists()) {
//                document.toObject(BestScore::class.java)?.bestScore ?: 0
//            } else {
//                0
//            }
//
//            if (newScore > currentBest) {
//                val updatedScore = BestScore(email = email, bestScore = newScore)
//                docRef.set(updatedScore)
//            }
//        }.addOnFailureListener {
//            Log.e("UpdateBestScore", "Error getting document", it)
//        }
//    }


//    fun getBestScore(id: Int, callback: (Int?) -> Unit) {
//        val email = auth.currentUser?.email.toString()
//
//        db.collection(AppConstants.games)
//            .document(id.toString())
//            .collection(id.toString())
//            .document(email)
//            .get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    val bestScore = document.toObject(BestScore::class.java)?.bestScore
//                    callback(bestScore)
//                } else {
//                    callback(0) // or null, if you prefer
//                }
//            }
//            .addOnFailureListener {
//                callback(null)
//            }
//    }


//    fun updateDailyStreak() {
//        val email = auth.currentUser?.email.toString()
//        val streakRef = db.collection(AppConstants.streak).document(email)
//
//        streakRef.get().addOnSuccessListener { document ->
//            val currentTime = System.currentTimeMillis()
//            val oneDayMillis = 24 * 60 * 60 * 1000L
//
//            if (document.exists()) {
//                val streak = document.toObject(Streak::class.java)
//                val lastTime = streak?.timestamp ?: 0
//                val diff = currentTime - lastTime
//
//                val newStreak = when {
//                    diff < oneDayMillis -> streak?.count ?: 1 // Already updated today
//                    diff < 2 * oneDayMillis -> (streak?.count ?: 0) + 1 // Daily streak continues
//                    else -> 1 // Reset streak
//                }
//
//                val updatedStreak = Streak(count = newStreak, timestamp = currentTime)
//                streakRef.set(updatedStreak)
//            } else {
//                val newStreak = Streak(count = 1, timestamp = currentTime)
//                streakRef.set(newStreak)
//            }
//        }.addOnFailureListener {
//        }
//    }


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


    fun saveUser(context: Context, user: UserResponse.UserData) {
        val gson = Gson()
        val json = gson.toJson(user)
        val sharedPref = context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putString(AppConstants.USER_KEY, json).apply()
    }

    fun getUser(context: Context): UserResponse.UserData? {

        fetchUserData(getUserId(context).toString(),context)

        val sharedPref = context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        val json = sharedPref.getString(AppConstants.USER_KEY, null) ?: return null
        return Gson().fromJson(json, UserResponse.UserData::class.java)
    }

    fun clearUser(context: Context) {
        val sharedPref = context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().remove(AppConstants.USER_KEY).apply()
    }


    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)
    }


    fun saveTips(context: Context, tips: Int) {
        getPrefs(context).edit().putInt(AppConstants.KEY_TIPS, tips).apply()
    }


    fun getTips(context: Context): Int {
        return getPrefs(context).getInt(AppConstants.KEY_TIPS, 0)
    }


    fun updateTips(context: Context, newTips: Int) {
        val current = getTips(context)
        saveTips(context, current + newTips)
        CoroutineScope(Dispatchers.Main).launch {
            val userId = getUserId(context).toString()
            val response = RetrofitClient.authInstance.updateTips(userId,current+newTips)
            if (response.isSuccessful) {
                Log.e("Response",response.toString())
            } else {
                Log.e("Response",response.toString())
            }
        }



    }

    /** Delete tips value */
    fun deleteTips(context: Context) {
        getPrefs(context).edit().remove(AppConstants.KEY_TIPS).apply()
    }

}