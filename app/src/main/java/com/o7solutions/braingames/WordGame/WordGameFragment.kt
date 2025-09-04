package com.o7solutions.braingames.WordGame

import android.app.AlertDialog
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.game.GameResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.o7solutions.braingames.R
import com.o7solutions.braingames.databinding.FragmentWordGameBinding
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import com.example.game.RetrofitInstance
import com.example.game.WordRepository
import com.o7solutions.braingames.DataClasses.GameFetchData
import com.o7solutions.braingames.DataClasses.Wordresponse
import com.o7solutions.braingames.Model.RetrofitClient
import com.o7solutions.braingames.utils.AppFunctions
import com.o7solutions.braingames.utils.NetworkChangeReceiver
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.random.Random

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class WordGameFragment : Fragment(), NetworkChangeReceiver.NetworkStateListener {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentWordGameBinding
    private lateinit var wordDisplayBox: FrameLayout
    private lateinit var wordTextView: TextView
    private lateinit var optionTextViews: List<TextView>
    private lateinit var pauseButton: ImageButton
    private lateinit var timeTextView: TextView
    private lateinit var progressTextView: TextView
    private lateinit var scoreTextView: TextView
    private lateinit var timerProgressBar: ProgressBar
    private lateinit var progressFeedbackTextView: TextView
    private lateinit var scoreFeedbackTextView: TextView
    private var score = 0
    private var hintCount = 3
    var playedSecond = 0
    private var currentWord: String = ""
    private var correctAnswer: String = ""
    private var originalWordList: List<String> = emptyList()
    private var remainingWords: MutableList<String> = mutableListOf()
    private var isAnswerable = true
    private var correctAnswersCount = 0
    private var currentLevel = 1

    // Fixed timer declaration - nullable instead of lateinit
    private var countDownTimer: CountDownTimer? = null
    private var wasManuallyPaused = false // Track manual vs automatic pause

    private var totalGameTime: Long = 60000L
    private var timeLeftInMillis: Long = totalGameTime
    private var isPaused = false
    private var loadingDialog: androidx.appcompat.app.AlertDialog? = null
    private lateinit var game: GameFetchData.Data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            currentLevel = it.getInt("level", 1)
            Log.d("Current level", currentLevel.toString())
            game = it.getSerializable("game_data") as GameFetchData.Data
        }

        if(currentLevel == 0) {
            currentLevel = 1
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWordGameBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch{
            val response = RetrofitClient.authInstance.updatePlayCount(game._id, game.playCount + 1)
            if (response.isSuccessful) {
                Log.d("Play Count", "Updated")
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.custom_exit_dialog, null)

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create()

            dialogView.findViewById<Button>(R.id.btnYes).setOnClickListener {
                dialog.dismiss()
                findNavController().popBackStack()
            }

            dialogView.findViewById<Button>(R.id.btnNo).setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        if (savedInstanceState == null && currentLevel == 1) score = 0

        updateTipData()
        wordDisplayBox = view.findViewById(R.id.wordDisplayBox)
        wordTextView = view.findViewById(R.id.wordTextView)
        pauseButton = view.findViewById(R.id.pauseButton)
        timeTextView = view.findViewById(R.id.timeTextView)
        progressTextView = view.findViewById(R.id.progressTextView)
        scoreTextView = view.findViewById(R.id.scoreTextView)
        timerProgressBar = view.findViewById(R.id.timerProgressBar)
        progressFeedbackTextView = view.findViewById(R.id.progressFeedbackTextView)
        scoreFeedbackTextView = view.findViewById(R.id.scoreFeedbackTextView)
        optionTextViews = listOf(
            view.findViewById(R.id.option1TextView),
            view.findViewById(R.id.option2TextView),
            view.findViewById(R.id.option3TextView),
            view.findViewById(R.id.option4TextView)
        )

        optionTextViews.forEach { it.setOnClickListener { v -> if (isAnswerable) checkAnswer(v as TextView) } }

        // Fixed pause button listener with logging
        pauseButton.setOnClickListener {
            Log.d("Timer", "Pause button clicked - Before toggle: isPaused=$isPaused")
            togglePause()
            Log.d("Timer", "Pause button clicked - After toggle: isPaused=$isPaused")
        }

        setupOnBackPressed()

        lifecycleScope.launch {
            if (isPaused) {
                Toast.makeText(requireActivity(), "Please resume the timer!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (loadLevelData(currentLevel)) {
                    startGame()
                } else {
                    // No words available → exit gracefully
                }
            }
        }

        binding.bulbButton.setOnClickListener {
            useHint()
            if (hintCount < 1) {
                Toast.makeText(requireContext(), "No tips available", Toast.LENGTH_SHORT).show()
            } else {
                AppFunctions.updateTips(requireActivity(), -1)
                updateTipData()
            }
        }
    }

    fun updateTipData() {
        hintCount = AppFunctions.getTips(requireActivity())
        updateHintCounter()
        binding.hintCounterTextView.text = hintCount.toString()
    }

    private suspend fun loadLevelData(level: Int): Boolean {
        // Don't increment currentLevel here since it's already done in startNextLevel()
        // Only set the time based on the level passed in
        when (level) {
            1 -> totalGameTime = 60000L
            2 -> totalGameTime = 60000L
            3 -> totalGameTime = 60000L
            4 -> totalGameTime = 60000L
            else -> totalGameTime = 60000L
        }

        timeLeftInMillis = totalGameTime

        return try {
            // For level 1, use length 5. For higher levels, use the appropriate length
            val wordLength = when (level) {
                1 -> 5
                2 -> 6
                3 -> 7
                4 -> 8
                else -> 5
            }

            val response = RetrofitClient.authInstance.getRandomWords(
                length = wordLength,
                count = 50,
                minLength = wordLength
            )

            if (response.isSuccessful) {
                WordRepository.wordList.clear()
                WordRepository.wordList.addAll(response.body()?.data ?: emptyList())

                val wordsForLevel = WordRepository.wordList

                if (wordsForLevel.size < 10) {
                    Toast.makeText(
                        requireActivity(),
                        "Not enough words were loaded for Level $level.",
                        Toast.LENGTH_LONG
                    ).show()
                    originalWordList = emptyList()
                    remainingWords.clear()
                    false
                } else {
                    originalWordList = wordsForLevel
                    remainingWords = originalWordList.shuffled().toMutableList()
                    true
                }
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("loadLevelData", "Error: ${e.message}")
            false
        }
    }

    private fun startGame() {
        correctAnswersCount = 0
        isPaused = false
        wasManuallyPaused = false
        updateProgress()

        // Safety: ensure pool is ready before first round
        if (!ensureWordPoolReady()) {
            Toast.makeText(
                requireActivity(),
                "No words available to start the game.",
                Toast.LENGTH_SHORT
            ).show()
            findNavController().popBackStack()
            return
        }

        setupNewRound()
        startTimer(timeLeftInMillis)
    }

    private fun ensureWordPoolReady(): Boolean {
        if (remainingWords.isEmpty()) {
            if (originalWordList.isEmpty()) return false
            remainingWords = originalWordList.shuffled().toMutableList()
        }
        return remainingWords.isNotEmpty()
    }

    private fun startNextLevel() {
        currentLevel++
        showLoadingDialog()

        val requiredLength = when (currentLevel) {
            2 -> 6
            3 -> 7
            4 -> 8
            else -> 5
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val wordsResponse = RetrofitClient.authInstance.getRandomWords(
                    length = requiredLength,
                    count = 50,
                    minLength = requiredLength
                )

                var wordsForLevel = emptyList<String>()
                if (wordsResponse.isSuccessful) {
                    wordsForLevel = wordsResponse.body()!!.data
                }
                if (wordsForLevel.size < 10) {
                    Toast.makeText(
                        requireActivity(),
                        "API did not provide enough words for the next level.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }
                WordRepository.wordList.clear()
                WordRepository.wordList.addAll(wordsForLevel)

                // Removed the isPaused check that was preventing level progression
                if (loadLevelData(currentLevel)) {
                    startGame()
                } else {
                    findNavController().popBackStack()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireActivity(),
                    "Failed to load words: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                findNavController().popBackStack()
            } finally {
                loadingDialog?.dismiss()
            }
        }
    }

    private fun setupNewRound() {
        // Safety checks to avoid IndexOutOfBounds
        if (!ensureWordPoolReady()) {
            Toast.makeText(requireActivity(), "No words available.", Toast.LENGTH_SHORT).show()
            gameOver()
            return
        }

        isAnswerable = true
        resetBoxBackgrounds()

        currentWord = remainingWords.removeAt(0)
        wordTextView.text = currentWord

        val options = generateOptions()
        // We'll show a shuffled copy but keep correctAnswer = options.first()
        correctAnswer = options.first()
        val display = options.shuffled()
        display.forEachIndexed { idx, option -> optionTextViews[idx].text = option }
    }

    private fun generateOptions(): List<String> {
        // Correct option: shuffled letters of the current word
        var shuffledCorrectAnswer: String
        do {
            shuffledCorrectAnswer = currentWord.toList().shuffled().joinToString("")
        } while (shuffledCorrectAnswer == currentWord && currentWord.length > 1)

        val wrongAnswers = mutableSetOf<String>()
        val alphabet = "abcdefghijklmnopqrstuvwxyz"
        while (wrongAnswers.size < 3) {
            val wordChars = currentWord.toMutableList()
            val positionToChange = Random.nextInt(wordChars.size)
            val originalChar = wordChars[positionToChange]
            var newChar: Char
            do {
                newChar = alphabet.random()
            } while (newChar == originalChar)
            wordChars[positionToChange] = newChar
            wrongAnswers.add(wordChars.joinToString(""))
        }
        return listOf(shuffledCorrectAnswer) + wrongAnswers.toList()
    }

    private fun checkAnswer(selectedView: TextView) {
        isAnswerable = false
        val isCorrect = selectedView.text.toString() == correctAnswer

        showProgressAnimation(isCorrect)
        showScoreAnimation(isCorrect)

        if (isCorrect) {
            correctAnswersCount++
            score += 20
            wordDisplayBox.setBackgroundResource(R.drawable.correct_answer_background)
            selectedView.setBackgroundResource(R.drawable.correct_answer_background)
        } else {
            score = max(0, score - 10)
            if (correctAnswersCount > 0) correctAnswersCount--
            wordDisplayBox.setBackgroundResource(R.drawable.wrong_answer_background)
            selectedView.setBackgroundResource(R.drawable.wrong_answer_background)
            optionTextViews.find { it.text.toString() == correctAnswer }
                ?.setBackgroundResource(R.drawable.correct_answer_background)
        }
        updateProgress()

        if (isCorrect && correctAnswersCount == 10) {
            stopTimer()
            Handler(Looper.getMainLooper()).postDelayed({ handleLevelCompletion() }, 1000)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({ setupNewRound() }, 1500)
        }
    }

    private fun showProgressAnimation(isCorrect: Boolean) {
        val view = progressFeedbackTextView
        if (isCorrect) {
            view.text = "+1"
            view.setTextColor(ContextCompat.getColor(requireActivity(), R.color.feedback_green))
        } else {
            if (correctAnswersCount > 0) {
                view.text = "-1"
                view.setTextColor(ContextCompat.getColor(requireActivity(), R.color.feedback_red))
            } else return
        }
        view.alpha = 1.0f
        view.translationY = 0f
        view.animate().alpha(0f).translationYBy(-60f).setDuration(800).withEndAction {
            view.translationY = 0f
        }.start()
    }

    private fun showScoreAnimation(isCorrect: Boolean) {
        val view = scoreFeedbackTextView
        if (isCorrect) {
            view.text = "+20"
            view.setTextColor(ContextCompat.getColor(requireActivity(), R.color.feedback_green))
        } else {
            if (score > 0) {
                view.text = "-10"
                view.setTextColor(ContextCompat.getColor(requireActivity(), R.color.feedback_red))
            } else return
        }
        view.alpha = 1.0f
        view.translationY = 0f
        view.animate()
            .alpha(0f)
            .translationYBy(-70f)
            .setDuration(800)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction { view.translationY = 0f }
            .start()
    }

    private fun updateProgress() {
        progressTextView.text = "$correctAnswersCount/10"
        scoreTextView.text = score.toString()
    }

    // Fixed timer methods
    private fun startTimer(time: Long) {
        Log.d("Timer", "startTimer called with ${time}ms")

        // Always cancel existing timer first
        countDownTimer?.cancel()
        countDownTimer = null

        countDownTimer = object : CountDownTimer(time, 100) {
            override fun onTick(millisUntilFinished: Long) {
                if (!isAdded) {
                    Log.d("Timer", "Timer tick cancelled - Fragment not attached")
                    return
                }

                // Double check if paused (safety)
                if (isPaused) {
                    Log.d("Timer", "Timer tick cancelled - Game is paused")
                    cancel()
                    return
                }

                timeLeftInMillis = millisUntilFinished
                updateTimerUI()
                playedSecond = ((totalGameTime - timeLeftInMillis) / 1000).toInt()

                // Log every 5 seconds for debugging
                if ((millisUntilFinished / 1000) % 5 == 0L) {
                    Log.d("Timer", "Timer: ${millisUntilFinished / 1000}s remaining")
                }
            }

            override fun onFinish() {
                if (!isAdded) return
                Log.d("Timer", "Timer finished")
                timeLeftInMillis = 0
                updateTimerUI()
                gameOver()
            }
        }.start()

        Log.d("Timer", "Timer started successfully")
    }

    private fun togglePause() {
        Log.d("Timer", "togglePause called - current isPaused: $isPaused")

        isPaused = !isPaused
        wasManuallyPaused = isPaused

        if (isPaused) {
            // Stop the timer
            countDownTimer?.cancel()
            countDownTimer = null
            pauseButton.setImageResource(R.drawable.ic_resume)
            isAnswerable = false
            Log.d("Timer", "✅ Timer PAUSED and CANCELLED")
        } else {
            // Resume the timer
            startTimer(timeLeftInMillis)
            pauseButton.setImageResource(R.drawable.ic_pause)
            isAnswerable = true
            Log.d("Timer", "✅ Timer RESUMED")
        }
    }

    private fun stopTimer() {
        Log.d("Timer", "stopTimer called")
        countDownTimer?.cancel()
        countDownTimer = null
        isPaused = true
    }

    private fun updateTimerUI() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        timeTextView.text = String.format("%02d:%02d", minutes, seconds)

        val progress = (timeLeftInMillis * 100 / totalGameTime).toInt()
        timerProgressBar.progress = progress
        val progressDrawable = timerProgressBar.progressDrawable as LayerDrawable
        val clipDrawable = progressDrawable.findDrawableByLayerId(android.R.id.progress)
        when {
            progress < 25 -> clipDrawable.setColorFilter(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.feedback_red
                ), PorterDuff.Mode.SRC_IN
            )

            progress < 50 -> clipDrawable.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN)
            else -> clipDrawable.setColorFilter(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.feedback_green
                ), PorterDuff.Mode.SRC_IN
            )
        }
    }

    private fun showLoadingDialog() {
        val dialogView =
            LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_loading, null)
        loadingDialog = MaterialAlertDialogBuilder(requireActivity())
            .setView(dialogView)
            .setCancelable(false)
            .show()
        loadingDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun gameOver() {
        Log.d("Timer", "gameOver called")

        isAnswerable = false
        stopTimer()

        GameResult.finalScore = score
        GameResult.allLevelsCompleted = false
        GameResult.timePlayedMillis = totalGameTime
        AppFunctions.updateUserDataThroughApi(
            score,
            false,
            totalGameTime - timeLeftInMillis,
            game._id,
            level = currentLevel,
            requireActivity(),
        )

        val bundle = Bundle().apply {
            putString("id", game._id)
            putString("score", score.toString())
        }

        val fragmentToGo = game.fragmentId
        val context = requireContext()
        val resId = context.resources?.getIdentifier(fragmentToGo, "id", context.packageName)

        resId?.let { destinationId ->
            val navOptions = NavOptions.Builder()
                .setPopUpTo(destinationId, true)
                .build()
            findNavController().navigate(R.id.gameEndFragment, bundle, navOptions)
        }
    }

    private fun handleLevelCompletion() {
        Log.d("Timer", "handleLevelCompletion called")
        stopTimer()

        if (currentLevel < 4) {
            val dialogView =
                LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_level_complete, null)
            val title = dialogView.findViewById<TextView>(R.id.levelCompleteTitle)
            title.text = "Level ${currentLevel} Cleared!"
            val dialog = MaterialAlertDialogBuilder(requireActivity())
                .setView(dialogView)
                .setCancelable(false)
                .create()
            dialog.show()
            dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            Handler(Looper.getMainLooper()).postDelayed({
                dialog.dismiss()
                startNextLevel()
            }, 2500)
        } else {
            GameResult.finalScore = score
            GameResult.allLevelsCompleted = true
            GameResult.timePlayedMillis = totalGameTime - timeLeftInMillis
            AppFunctions.updateUserDataThroughApi(
                score,
                true,
                (totalGameTime - timeLeftInMillis) * 1000,
                game._id,
                level = currentLevel,
                requireActivity()
            )
            val bundle = Bundle().apply {
                putString("id", game._id)
                putString("score", score.toString())
            }

            val fragmentToGo = game.fragmentId
            val context = requireContext()
            val resId = context.resources?.getIdentifier(fragmentToGo, "id", context.packageName)

            resId?.let { destinationId ->
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(destinationId, true) // clear backstack
                    .build()

                findNavController().navigate(R.id.gameEndFragment, bundle, navOptions)
            }
        }
    }

    private fun resetBoxBackgrounds() {
        wordDisplayBox.setBackgroundResource(R.drawable.neon_box_background)
        optionTextViews.forEach { it.setBackgroundResource(R.drawable.neon_box_background) }
    }

    private fun setupOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, // tie to view lifecycle to avoid leaks
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!isPaused) togglePause()
                    showExitConfirmationDialog()
                }
            }
        )
    }

    private fun updateHintCounter() = with(binding) {
        hintCounterTextView.text = hintCount.toString()
        bulbButton.isEnabled = hintCount > 0
        bulbButton.alpha = if (hintCount <= 0) 0.5f else 1.0f
    }

    private fun useHint() {
        if (hintCount <= 0 || !isAnswerable || isPaused) return

        val correctOptionView = optionTextViews.find { it.text.toString() == correctAnswer }
        correctOptionView?.let { correctView ->
            isAnswerable = false
            correctView.setBackgroundResource(R.drawable.correct_answer_background)
            wordDisplayBox.setBackgroundResource(R.drawable.correct_answer_background)

            correctAnswersCount++
            score += 30

            showProgressAnimation(true)
            showScoreAnimation(true)
            updateProgress()

            Handler(Looper.getMainLooper()).postDelayed({
                if (correctAnswersCount == 10) {
                    stopTimer()
                    handleLevelCompletion()
                } else {
                    setupNewRound()
                }
            }, 2000)
        }
    }

    private fun showExitConfirmationDialog() {
        if (!isAdded) return
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.custom_exit_dialog, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<Button>(R.id.btnYes).setOnClickListener {
            dialog.dismiss()
            findNavController().popBackStack()
        }

        dialogView.findViewById<Button>(R.id.btnNo).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun gameExit() {
        timeLeftInMillis = 0
        updateTimerUI()
        gameOver()
    }

    // Fixed lifecycle methods
    override fun onStart() {
        super.onStart()
        Log.d("Timer", "onStart - isPaused: $isPaused, wasManuallyPaused: $wasManuallyPaused")

        NetworkChangeReceiver.networkStateListener = this

        // Only auto-start if not manually paused
        if (!isPaused || !wasManuallyPaused) {
            if (isPaused) {
                isPaused = false
                wasManuallyPaused = false
            }
            startTimer(timeLeftInMillis)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("Timer", "onStop called")

        NetworkChangeReceiver.networkStateListener = null
        stopTimer()
    }

    override fun onPause() {
        super.onPause()
        Log.d("Timer", "onPause called - isPaused: $isPaused")

        // Only auto-pause if not already manually paused
        if (!isPaused) {
            wasManuallyPaused = false // This is automatic pause
            togglePause()
        }
        loadingDialog?.dismiss()
    }

    override fun onResume() {
        super.onResume()
        Log.d("Timer", "onResume called - isPaused: $isPaused, wasManuallyPaused: $wasManuallyPaused")

        // Resume if it was automatically paused (not manually)
        if (isPaused && !wasManuallyPaused) {
            togglePause()
        }
    }

    // Fixed network callbacks
    override fun onNetworkAvailable() {
        Log.d("Timer", "Network available - isPaused: $isPaused, wasManuallyPaused: $wasManuallyPaused")

        // Only resume if paused due to network loss (not manual pause)
        if (isPaused && !wasManuallyPaused) {
            togglePause()
        }
    }

    override fun onNetworkLost() {
        Log.d("Timer", "Network lost - isPaused: $isPaused")

        // Pause if currently running
        if (!isPaused) {
            wasManuallyPaused = false // This is automatic pause
            togglePause()
        }
    }
}