package com.o7solutions.braingames.WordGame

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
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
import com.o7solutions.braingames.DataClasses.Games
import com.o7solutions.braingames.utils.AppFunctions
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WordGameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WordGameFragment : Fragment() {
    // TODO: Rename and change types of parameters
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

    private var currentWord: String = ""
    private var correctAnswer: String = ""
    private var originalWordList: List<String> = listOf()
    private var remainingWords: MutableList<String> = mutableListOf()
    private var isAnswerable = true
    private var correctAnswersCount = 0
    private var currentLevel = 1
    private lateinit var countDownTimer: CountDownTimer
    private var totalGameTime: Long = 120000L
    private var timeLeftInMillis: Long = totalGameTime
    private var isPaused = false
    private var loadingDialog: androidx.appcompat.app.AlertDialog? = null
    private lateinit var game: GameFetchData.Data


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            currentLevel = it.getInt("SELECTED_LEVEL", 1)
//            game = it.getSerializable("game_data") as Games
            game = it.getSerializable("game_data") as GameFetchData.Data


        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWordGameBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            if (currentLevel == 1) score = 0
        }

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
        optionTextViews.forEach { it.setOnClickListener { view -> if (isAnswerable) checkAnswer(view as TextView) } }
        pauseButton.setOnClickListener { togglePause() }
//        initViews()
        setupOnBackPressed()

        if (isPaused) {
            Toast.makeText(requireActivity(), "Please resume the timer!", Toast.LENGTH_SHORT).show()
        } else {
            if (loadLevelData(currentLevel)) {
                startGame()
            } else {
                findNavController().popBackStack()
            }
        }

        pauseButton.setOnClickListener { togglePause() }
        binding.bulbButton.setOnClickListener { useHint() }

        // Load hint count from SharedPreferences
        loadHintCount()
        updateHintCounter()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WordGameFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WordGameFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun loadLevelData(level: Int): Boolean {
        totalGameTime = when (level) {
            1 -> 120000L
            2 -> 110000L
            3 -> 100000L
            4 -> 90000L
            else -> 120000L
        }
        timeLeftInMillis = totalGameTime


        val wordsForLevel = WordRepository.wordList

        if (wordsForLevel.size < 10) {
            Toast.makeText(
                requireActivity(),
                "Not enough words were loaded for Level $level.",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        originalWordList = wordsForLevel
        remainingWords = originalWordList.toMutableList()
        return true
    }

//    private fun initViews() {
//        wordDisplayBox = findViewById(R.id.wordDisplayBox)
//        wordTextView = findViewById(R.id.wordTextView)
//        pauseButton = findViewById(R.id.pauseButton)
//        timeTextView = findViewById(R.id.timeTextView)
//        progressTextView = findViewById(R.id.progressTextView)
//        scoreTextView = findViewById(R.id.scoreTextView)
//        timerProgressBar = findViewById(R.id.timerProgressBar)
//        progressFeedbackTextView = findViewById(R.id.progressFeedbackTextView)
//        scoreFeedbackTextView = findViewById(R.id.scoreFeedbackTextView)
//        bulbButton = findViewById(R.id.bulbButton)
//        hintCounterTextView = findViewById(R.id.hintCounterTextView)
//        optionTextViews = listOf(
//            findViewById(R.id.option1TextView),
//            findViewById(R.id.option2TextView),
//            findViewById(R.id.option3TextView),
//            findViewById(R.id.option4TextView)
//        )
//        optionTextViews.forEach { it.setOnClickListener { view -> if (isAnswerable) checkAnswer(view as TextView) } }
//        pauseButton.setOnClickListener { togglePause() }
//        bulbButton.setOnClickListener { useHint() }
//
//        // Load hint count from SharedPreferences
//        loadHintCount()
//        updateHintCounter()
//    }
    private fun startGame() {
        correctAnswersCount = 0
        isPaused = false
        updateProgress()
        setupNewRound()
        startTimer(timeLeftInMillis)
    }

    private fun startNextLevel() {
        currentLevel++
        showLoadingDialog()

        val requiredLength = when (currentLevel) {
            2 -> 6
            3 -> 7
            4 -> 8
            else -> 0
        }

        lifecycleScope.launch {
            try {
                val wordsForLevel =
                    RetrofitInstance.api.getRandomWords(length = requiredLength, count = 50)
                if (wordsForLevel.size < 10) {
                    Toast.makeText(
                        requireActivity(),
                        "API did not provide enough words for the next level.",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                    return@launch
                }
                WordRepository.wordList = wordsForLevel

                if (isPaused) {
                    Toast.makeText(
                        requireActivity(),
                        "Please resume the Timer!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }
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
//        if (isPaused) {
//            Toast.makeText(requireActivity(), "Please resume the timer!", Toast.LENGTH_SHORT).show()
//            return
//        }
        if (remainingWords.isEmpty()) {
            remainingWords = originalWordList.shuffled().toMutableList()
        }


        isAnswerable = true
        resetBoxBackgrounds()
        currentWord = remainingWords.removeAt(0)
        wordTextView.text = currentWord
        val options = generateOptions()
        correctAnswer = options.first()
        options.shuffled()
            .forEachIndexed { index, option -> optionTextViews[index].text = option }


    }

    private fun generateOptions(): List<String> {
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
            val fakeWord = wordChars.joinToString("")
            wrongAnswers.add(fakeWord)
        }
        val finalOptions = mutableListOf(shuffledCorrectAnswer)
        finalOptions.addAll(wrongAnswers)
        return finalOptions
    }

    private fun checkAnswer(selectedView: TextView) {
        isAnswerable = false
        val isCorrect = selectedView.text.toString() == correctAnswer

        showProgressAnimation(isCorrect)
        showScoreAnimation(isCorrect)

        if (isCorrect) {
            correctAnswersCount++
            score += 30
            wordDisplayBox.setBackgroundResource(R.drawable.correct_answer_background)
            selectedView.setBackgroundResource(R.drawable.correct_answer_background)
        } else {
            score = max(0, score - 10)
            if (correctAnswersCount > 0) {
                correctAnswersCount--
            }
            wordDisplayBox.setBackgroundResource(R.drawable.wrong_answer_background)
            selectedView.setBackgroundResource(R.drawable.wrong_answer_background)
            val correctOptionView = optionTextViews.find { it.text.toString() == correctAnswer }
            correctOptionView?.setBackgroundResource(R.drawable.correct_answer_background)
        }
        updateProgress()
        if (isCorrect && correctAnswersCount == 10) {
            countDownTimer.cancel()
            Handler(Looper.getMainLooper()).postDelayed({
                handleLevelCompletion()
            }, 1000)

        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                setupNewRound()
            }, 1500)
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
        view.animate()
            .alpha(0f)
            .translationYBy(-60f)
            .setDuration(800)
            .withEndAction { view.translationY = 0f }
            .start()
    }

    private fun showScoreAnimation(isCorrect: Boolean) {
        val view = scoreFeedbackTextView
        if (isCorrect) {
            view.text = "+30"
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

    private fun startTimer(time: Long) {
        countDownTimer = object : CountDownTimer(time, 100) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerUI()
            }

            override fun onFinish() {
                timeLeftInMillis = 0
                updateTimerUI()
                gameOver()
            }
        }.start()
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

    private fun togglePause() {
        isPaused = !isPaused
        if (isPaused) {
            countDownTimer.cancel()
            pauseButton.setImageResource(R.drawable.ic_resume)
            // Disable option selection when paused
            isAnswerable = false
        } else {
            startTimer(timeLeftInMillis)
            pauseButton.setImageResource(R.drawable.ic_pause)
            // Re-enable option selection when resumed
            isAnswerable = true
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
        isAnswerable = false
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
        GameResult.finalScore = score
        GameResult.allLevelsCompleted = false
        GameResult.timePlayedMillis = totalGameTime
        AppFunctions.updateUserDataThroughApi(score,false,totalGameTime - timeLeftInMillis,game._id,requireActivity())

//        AppFunctions.updateUserData(score, false, totalGameTime,game._id!!.toInt())
        var bundle = Bundle().apply {
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

    private fun handleLevelCompletion() {
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
//            AppFunctions.updateUserData(score, true, totalGameTime - timeLeftInMillis,game._id!!.toInt())
            AppFunctions.updateUserDataThroughApi(score,true,totalGameTime - timeLeftInMillis,game._id,requireActivity())

//            here update score
            findNavController().popBackStack()
        }
    }

    private fun resetBoxBackgrounds() {
        wordDisplayBox.setBackgroundResource(R.drawable.neon_box_background)
        optionTextViews.forEach { textView ->
            textView.setBackgroundResource(R.drawable.neon_box_background)
        }
    }

    private fun setupOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!isPaused) togglePause()
                    showExitConfirmationDialog()
                }
            })
    }

//    private fun updateProgress() {
//        progressTextView.text = "$correctAnswersCount/10"
//        scoreTextView.text = score.toString()
//    }

    private fun loadHintCount() {
        val sharedPrefs = requireActivity().getSharedPreferences("GamePrefs", Context.MODE_PRIVATE)
        hintCount = sharedPrefs.getInt("hint_count", 3)
    }

    private fun saveHintCount() {
        val sharedPrefs = requireActivity().getSharedPreferences("GamePrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putInt("hint_count", hintCount).apply()
    }

    private fun updateHintCounter() {

        binding.apply {


        hintCounterTextView.text = hintCount.toString()
        bulbButton.isEnabled = hintCount > 0
        if (hintCount <= 0) {
            bulbButton.alpha = 0.5f
        } else {
            bulbButton.alpha = 1.0f
        }
        }
    }

    private fun useHint() {
        if (hintCount <= 0 || !isAnswerable || isPaused) return

        hintCount--
        saveHintCount()
        updateHintCounter()

        // Find the correct answer and highlight it
        val correctOptionView = optionTextViews.find { it.text.toString() == correctAnswer }
        correctOptionView?.let { correctView ->
            // Mark as answered
            isAnswerable = false

            // Show correct answer in green
            correctView.setBackgroundResource(R.drawable.correct_answer_background)
            wordDisplayBox.setBackgroundResource(R.drawable.correct_answer_background)

            // Add points and progress
            correctAnswersCount++
            score += 30

            // Show feedback animations
            showProgressAnimation(true)
            showScoreAnimation(true)

            // Update progress
            updateProgress()

            // Auto-advance to next question after 2 seconds
            Handler(Looper.getMainLooper()).postDelayed({
                if (correctAnswersCount == 10) {
                    handleLevelCompletion()
                } else {
                    setupNewRound()
                }
            }, 2000)
        }
    }


    private fun showExitConfirmationDialog() {

        if (!isAdded) return
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Exit Game?")
            .setMessage("Are you sure you want to exit?")
            .setNegativeButton("No") { _, _ -> if (isPaused) togglePause() }
            .setPositiveButton("Yes") { _, _ -> findNavController().popBackStack() }
            .setOnCancelListener { if (isPaused) togglePause() }
            .show()
    }

    override fun onPause() {
        super.onPause()
        if (!isPaused) {
            togglePause()
        }
        loadingDialog?.dismiss()
    }
}