package com.o7solutions.braingames.FindTheWord

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.zigzag.GridCellView
import com.example.zigzag.LevelCache
import com.example.zigzag.WordSearchGridView
import com.google.android.material.button.MaterialButton
import com.o7solutions.braingames.R
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private const val ARG_LEVEL_NUMBER = "level_number"
private const val GRID_SIZE = 6

class GameFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var levelNumber = 1
    private lateinit var pauseButton: ImageView
    private lateinit var hintButton: ImageView
    private lateinit var hintCounter: TextView
    private lateinit var wordsContainer: LinearLayout
    private lateinit var wordSearchGrid: WordSearchGridView
    private var targetWords = mutableListOf<String>()
    private var foundWords = mutableSetOf<String>()
    private var totalHintsRemaining = 1
    private var hintedWord = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            levelNumber = it.getInt(ARG_LEVEL_NUMBER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val levelHeadingTextView = view.findViewById<TextView>(R.id.tv_level_heading)
        levelHeadingTextView.text = "Level $levelNumber"

        pauseButton = view.findViewById(R.id.iv_pause)
        hintButton = view.findViewById(R.id.iv_hint)
        hintCounter = view.findViewById(R.id.tv_hint_counter)
        wordsContainer = view.findViewById(R.id.ll_words_container)
        wordSearchGrid = view.findViewById(R.id.word_search_grid)

        pauseButton.setOnClickListener {
            showPauseDialog()
        }

        hintButton.setOnClickListener {
            useHint()
        }

        loadHintState()
        updateHintCounter()
        loadLevelDataFromCache()
        wordSearchGrid.setOnWordFoundListener { word, colorIndex ->
            handleWordFound(word, colorIndex)
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showLeaveConfirmationDialog()
            }
        })
    }

    private fun loadLevelDataFromCache() {
        if (levelNumber == null) return
        val currentLevel = LevelCache.levels?.find { it.levelNumber == levelNumber }
        if (currentLevel != null) {
            targetWords = currentLevel.words.shuffled().take(4).toMutableList()
            displayWordsInContainer(targetWords)
            val gridCharacters = generateGrid(targetWords)
            populateGridUI(gridCharacters)
            wordSearchGrid.setTargetWords(targetWords)

            // Restore hinted word if exists
            if (hintedWord.isNotEmpty() && targetWords.contains(hintedWord)) {
                wordSearchGrid.highlightWordPermanent(hintedWord)
            }
            updateWordsDisplay()
        }
    }

    private fun generateGrid(words: List<String>): Array<Array<Char>> {
        val grid = Array(GRID_SIZE) { Array(GRID_SIZE) { ' ' } }

        words.forEach { word ->
            var placed = false
            val reversedWord = word.reversed()
            for (attempt in 1..200) {
                val direction = Random.nextInt(2)
                val maxRow = if (direction == 1) GRID_SIZE - reversedWord.length else GRID_SIZE - 1
                val maxCol = if (direction == 0) GRID_SIZE - reversedWord.length else GRID_SIZE - 1
                if (maxRow < 0 || maxCol < 0) continue
                val row = Random.nextInt(maxRow + 1)
                val col = Random.nextInt(maxCol + 1)

                if (canPlaceWord(grid, reversedWord, row, col, direction)) {
                    placeWord(grid, reversedWord, row, col, direction)
                    placed = true
                    break
                }
            }
            if (!placed) {
                for (attempt in 1..100) {
                    val direction = Random.nextInt(2)

                    val maxRow = if (direction == 1) GRID_SIZE - word.length else GRID_SIZE - 1
                    val maxCol = if (direction == 0) GRID_SIZE - word.length else GRID_SIZE - 1

                    if (maxRow < 0 || maxCol < 0) continue

                    val row = Random.nextInt(maxRow + 1)
                    val col = Random.nextInt(maxCol + 1)

                    if (canPlaceWord(grid, word, row, col, direction)) {
                        placeWord(grid, word, row, col, direction)
                        placed = true
                        break
                    }
                }
            }
        }
        for (r in 0 until GRID_SIZE) {
            for (c in 0 until GRID_SIZE) {
                if (grid[r][c] == ' ') {
                    grid[r][c] = ('A'..'Z').random()
                }
            }
        }
        return grid
    }

    private fun canPlaceWord(grid: Array<Array<Char>>, word: String, row: Int, col: Int, direction: Int): Boolean {
        if (direction == 0) {
            if (col + word.length > GRID_SIZE) return false
        } else {
            if (row + word.length > GRID_SIZE) return false
        }

        for (i in word.indices) {
            val r = row + if (direction == 1) i else 0
            val c = col + if (direction == 0) i else 0

            if (r >= GRID_SIZE || c >= GRID_SIZE) return false
            if (grid[r][c] != ' ' && grid[r][c] != word[i]) return false
        }
        return true
    }

    private fun placeWord(grid: Array<Array<Char>>, word: String, row: Int, col: Int, direction: Int) {
        for (i in word.indices) {
            val r = row + if (direction == 1) i else 0
            val c = col + if (direction == 0) i else 0
            grid[r][c] = word[i]
        }
    }

    private fun populateGridUI(gridChars: Array<Array<Char>>) {
        wordSearchGrid.removeAllViews()
        wordSearchGrid.columnCount = GRID_SIZE
        wordSearchGrid.rowCount = GRID_SIZE

        for (r in 0 until GRID_SIZE) {
            for (c in 0 until GRID_SIZE) {
                val cell = GridCellView(requireContext()).apply {
                    text = gridChars[r][c].toString()
                }

                val params = GridLayout.LayoutParams(
                    GridLayout.spec(r, 1f),
                    GridLayout.spec(c, 1f)
                ).apply {
                    width = 0
                    height = 0
                }
                cell.layoutParams = params
                wordSearchGrid.addView(cell)
            }
        }
    }

    private fun displayWordsInContainer(words: List<String>) {
        wordsContainer.removeAllViews()
        for (word in words) {
            val textView = TextView(context).apply {
                text = word
                textSize = 16f
                setTextColor(Color.WHITE)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 24
                }
                layoutParams = params
            }
            wordsContainer.addView(textView)
        }
    }

    private fun handleWordFound(word: String, colorIndex: Int) {
        if (targetWords.contains(word) && !foundWords.contains(word)) {
            foundWords.add(word)
            wordSearchGrid.setNextColor()
            updateWordDisplay(word)
            if (word == hintedWord) {
                wordSearchGrid.removeHint(word)
                hintedWord = ""
            }

            Toast.makeText(context, "Great You have Found: $word!", Toast.LENGTH_SHORT).show()

            if (foundWords.size == targetWords.size) {
                showLevelCompleteDialog()
            }
        } else if (foundWords.contains(word)) {
            Toast.makeText(context, "Already found!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Try again!", Toast.LENGTH_SHORT).show()
        }
    }



    private fun updateWordsDisplay() {
        for (i in 0 until wordsContainer.childCount) {
            val textView = wordsContainer.getChildAt(i) as TextView
            val word = textView.text.toString()
            if (foundWords.contains(word)) {
                textView.setTextColor(Color.GREEN)
            } else if (word == hintedWord) {
                textView.setTextColor(Color.GREEN)
            } else {
                textView.setTextColor(Color.WHITE)
            }
        }
    }

    private fun updateWordDisplay(foundWord: String) {
        for (i in 0 until wordsContainer.childCount) {
            val textView = wordsContainer.getChildAt(i) as TextView
            if (textView.text == foundWord) {
                textView.setTextColor(Color.GREEN)
                break
            }
        }
    }

    private fun showPauseDialog() {
        pauseButton.setImageResource(R.drawable.ic_play_arrow)
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_pause, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.setOnDismissListener {
            pauseButton.setImageResource(R.drawable.ic_pause)
        }
        val continueButton = dialogView.findViewById<MaterialButton>(R.id.btn_continue)
        val restartButton = dialogView.findViewById<MaterialButton>(R.id.btn_restart)
        val leaveButton = dialogView.findViewById<MaterialButton>(R.id.btn_leave)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setDimAmount(0.85f)
        continueButton.setOnClickListener {
            dialog.dismiss()
        }
        restartButton.setOnClickListener {
            dialog.dismiss()
            restartGame()
        }
        leaveButton.setOnClickListener {
            dialog.dismiss()
            showLeaveConfirmationDialog()
        }
        dialog.show()
    }

    private fun restartGame() {
        foundWords.clear()
        wordSearchGrid.resetSelection()
        loadLevelDataFromCache()
    }

    private fun showLevelCompleteDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_level_complete, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setDimAmount(0.3f)

        dialog.show()

        dialogView.postDelayed({
            if (dialog.isShowing) {
                dialog.dismiss()
                startNextLevelOrGoHome()
            }
        }, 3500)
    }

    private fun startNextLevelOrGoHome() {
        val nextLevelNumber = (levelNumber ?: 1) + 1

        val nextLevelExists = LevelCache.levels?.any { it.levelNumber == nextLevelNumber } == true

        if (nextLevelExists) {
            val nextLevelFragment = GameFragment.newInstance(nextLevelNumber)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, nextLevelFragment)
                .addToBackStack(null)
                .commit()
        } else {
            parentFragmentManager.popBackStack(null, 0)
        }
    }

    private fun showLeaveConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Leave Game")
            .setMessage("Are you sure you want to leave? Your progress will be lost.")
            .setPositiveButton("Yes") { _, _ ->
                resetLevelProgress()
                parentFragmentManager.popBackStack(null, 0)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun resetLevelProgress() {
        foundWords.clear()
        wordSearchGrid.resetSelection()
        loadLevelDataFromCache()
    }

    private fun useHint() {
        if (totalHintsRemaining <= 0) {
            Toast.makeText(context, "No hints remaining!", Toast.LENGTH_SHORT).show()
            return
        }

        val remainingWords = targetWords.filter { !foundWords.contains(it) && it != hintedWord }
        if (remainingWords.isNotEmpty()) {
            val randomWord = remainingWords.random()
            hintedWord = randomWord
            revealWordInGrid(randomWord)
            totalHintsRemaining--
            saveHintState()
            updateHintCounter()
            updateWordsDisplay()
            Toast.makeText(context, "Hint used! Find: $randomWord", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "All words found!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun revealWordInGrid(word: String) {
        wordSearchGrid.highlightWord(word)
    }

    private fun loadHintState() {
        val sharedPrefs = requireContext().getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        totalHintsRemaining = sharedPrefs.getInt("total_hints_remaining", 1)
        hintedWord = sharedPrefs.getString("hinted_word", "") ?: ""
    }

    private fun saveHintState() {
        val sharedPrefs = requireContext().getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit()
            .putInt("total_hints_remaining", totalHintsRemaining)
            .putString("hinted_word", hintedWord)
            .apply()
    }

    private fun updateHintCounter() {
        hintCounter.text = totalHintsRemaining.toString()
    }

    companion object {
        @JvmStatic
        fun newInstance(levelNumber: Int) =
            GameFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_LEVEL_NUMBER, levelNumber)
                }
            }
    }
}