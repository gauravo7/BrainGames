package com.o7solutions.braingames.WordGame

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.game.GameResult
import com.example.game.RetrofitInstance
import com.example.game.WordRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.o7solutions.braingames.DataClasses.GameFetchData
import com.o7solutions.braingames.DataClasses.Games
import com.o7solutions.braingames.R
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var loadingDialog: androidx.appcompat.app.AlertDialog? = null
    private lateinit var level1Card: CardView
    private lateinit var level2Card: CardView
    private lateinit var level3Card: CardView
    private lateinit var level4Card: CardView
    private lateinit var game: GameFetchData.Data


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            game = it.getSerializable("game_data") as GameFetchData.Data

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        level1Card = view.findViewById(R.id.level1Card)
        level2Card = view.findViewById(R.id.level2Card)
        level3Card = view.findViewById(R.id.level3Card)
        level4Card = view.findViewById(R.id.level4Card)

        level1Card.setOnClickListener { startGameWithLevel(1) }
        level2Card.setOnClickListener { startGameWithLevel(2) }
        level3Card.setOnClickListener { startGameWithLevel(3) }
        level4Card.setOnClickListener { startGameWithLevel(4) }

        animateCardsIn()

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(requireContext())
                    .setTitle("Exit?")
                    .setMessage("Do you want to go back?")
                    .setPositiveButton("Yes") { _, _ ->
                        findNavController().popBackStack()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }

        })
    }

    private fun animateCardsIn() {
        val cards = listOf(level1Card, level2Card, level3Card, level4Card)
        cards.forEachIndexed { index, card ->
            card.animate()
                .alpha(1f)
                .setDuration(400)
                .setStartDelay((200 + (index * 100)).toLong())
                .start()
        }
    }

    private fun showResultsDialog() {
        val score = GameResult.finalScore
        val allLevelsCompleted = GameResult.allLevelsCompleted
        val timePlayed = GameResult.timePlayedMillis

        val message = "Total Score: $score\n" +
                "Time Played: $timePlayed ms\n" +
                "Completed All Levels: $allLevelsCompleted"

        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Game Over")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .setOnDismissListener {
                GameResult.clear()
            }
            .show()
    }

    private fun showLoadingDialog() {
        val dialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_loading, null)
        loadingDialog = MaterialAlertDialogBuilder(requireActivity())
            .setView(dialogView)
            .setCancelable(false)
            .show()
        loadingDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    private fun startGameWithLevel(level: Int) {
        showLoadingDialog()
        val requiredLength = when (level) {
            1 -> 5
            2 -> 6
            3 -> 7
            4 -> 8
            else -> 0
        }

        lifecycleScope.launch {
            try {
                val wordsForLevel = RetrofitInstance.api.getRandomWords(length = requiredLength, count = 50)

                if (wordsForLevel.size < 10) {
                    Toast.makeText(requireActivity(), "API did not provide enough words.", Toast.LENGTH_SHORT).show()
                    loadingDialog?.dismiss()
                    return@launch
                }

                WordRepository.wordList = wordsForLevel

                val bundle = Bundle()
                bundle.putInt("SELECTED_LEVEL",level)
                bundle.putSerializable("game_data", game)
                findNavController().navigate(R.id.wordGameFragment,bundle)

            } catch (e: Exception) {
                Toast.makeText(requireActivity(), "Failed to load words: ${e.message}", Toast.LENGTH_LONG).show()
                loadingDialog?.dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (GameResult.finalScore != null) {
            showResultsDialog()
        }
    }

    override fun onPause() {
        super.onPause()
        loadingDialog?.dismiss()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DashboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}