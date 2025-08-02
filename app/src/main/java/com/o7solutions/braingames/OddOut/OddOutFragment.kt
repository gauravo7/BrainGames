package com.o7solutions.braingames.OddOut

import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.o7solutions.braingames.DataClasses.Games
import com.o7solutions.braingames.R
import com.o7solutions.braingames.databinding.FragmentOddOutBinding
import com.o7solutions.braingames.utils.AppFunctions

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OddOutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OddOutFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentOddOutBinding
    private lateinit var adapter: OddOutAdapter
    private var points = 0
    private var totalSeconds = 60
    private var countDownTimer: CountDownTimer? = null
    private lateinit var game: Games
    var level = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            game = it.getSerializable("game_data") as Games

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOddOutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        startTimer()
        binding.pointsText.text = "$points"



//        Show dialog on back click
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OddOutFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OddOutFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDetach() {
        super.onDetach()
        countDownTimer?.cancel()
    }

    private fun setupRecyclerView() {
        if(points >=100) {
            level = 2
        } else if(points >= 200) {
            level = 3
        } else if(points >= 300) {
            level = 4
        }
        // Example drawable resources
//        val images = listOf(
//            R.drawable.rectangle, R.drawable.circle, R.drawable.star,
//            R.drawable.circle, R.drawable.rectangle, R.drawable.star
//        )
//        val answerIndex = 1 // Assume the odd one is at index 1
//        val itemCount = images.size

        lateinit var listOfIndexes : List<Int>
        if(level == 1) {
            listOfIndexes = GameLib.getUniqueRandomNumbers(0, 15, 5)
        } else if(level == 2) {
            listOfIndexes = GameLib.getUniqueRandomNumbers(0, 15, 7)
        } else if(level == 3) {
            listOfIndexes = GameLib.getUniqueRandomNumbers(0, 15, 9)
        } else if(level == 4) {
            listOfIndexes = GameLib.getUniqueRandomNumbers(0, 15, 11)

        }
        var answerIndex = GameLib.getRandomNumberFromList(listOfIndexes)
        val images = listOf(
            R.drawable.rectangle, R.drawable.circle, R.drawable.star, R.drawable.cone
        )
        var answerIndexImage = AppFunctions.returnRandom(0, 4)
        Log.d("Answer Index", answerIndexImage.toString())

        adapter = OddOutAdapter(
            listOfIndexes,
            answerIndex,
            16,
            answerIndexImage,
            object : OddOutAdapter.OnClick {
                override fun onImageClick(isCorrect: Boolean) {
                    if (isCorrect) {
                        points += 20
                    } else {
                        points -= 10
                    }
                    binding.pointsText.text = "$points"


                    setupRecyclerView()

                }
            })

//        Toast.makeText(requireContext(), answerIndex.toString(), Toast.LENGTH_SHORT).show()

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 4) // 3 columns
        binding.recyclerView.adapter = adapter
    }

    private fun startTimer() {
        countDownTimer?.cancel()
        binding.timeText.text = "\u23F3 $totalSeconds"
        binding.seekBarBrightness.max = totalSeconds
        binding.seekBarBrightness.progress = totalSeconds

        countDownTimer = object : CountDownTimer(totalSeconds * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                binding.timeText.text = "\u23F3 $secondsLeft"
                binding.seekBarBrightness.progress = secondsLeft
            }

            override fun onFinish() {

                if (isAdded) {


                    AppFunctions.updateUserData(points,true,60000,game.id!!.toInt())
                    binding.timeText.text = "\u23F3 0"
                    Toast.makeText(
                        requireContext(),
                        "Time's up!\nScore: $points",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    binding.recyclerView.isEnabled = false
                    binding.seekBarBrightness.progress = 0
                    showGameOverDialog()
                }
            }
        }.start()
    }

    private fun showGameOverDialog() {
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Time's up!")
            .setMessage("Your final score: $points")
            .setCancelable(false)
            .setPositiveButton("Play Again") { _, _ ->
                points = 0
                binding.pointsText.text = "$points"
                setupRecyclerView()
                startTimer()
            }
            .setNegativeButton("Exit") { _, _ ->
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            .create()
        dialog.show()
    }


}