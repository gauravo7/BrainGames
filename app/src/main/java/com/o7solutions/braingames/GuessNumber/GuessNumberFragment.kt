package com.o7solutions.braingames.GuessNumber

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputFilter
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.o7solutions.braingames.DataClasses.GameFetchData
import com.o7solutions.braingames.DataClasses.Games
import com.o7solutions.braingames.R
import com.o7solutions.braingames.databinding.FragmentGuessNumberBinding
import com.o7solutions.braingames.utils.AppFunctions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GuessNumberFragment : Fragment() {

    private var points = 0
    private var level = 1
    private var lowRange = 100
    private var highRange = 1000
    private var actualNumber = 0
    private lateinit var binding: FragmentGuessNumberBinding
    private lateinit var moveUp: Animation
    private lateinit var moveDown: Animation
    private var totalSeconds = 60
    private var countDownTimer: CountDownTimer? = null
    var newMaxLength = 3
    private lateinit var game : GameFetchData.Data
    var hint = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            game = it.getSerializable("game_data") as GameFetchData.Data
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGuessNumberBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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
        binding.points.text = "0"
//        binding.level.text = "Level $level"
        moveUp = AnimationUtils.loadAnimation(requireContext(), R.anim.move_up)
        moveDown = AnimationUtils.loadAnimation(requireContext(), R.anim.move_down)



        binding.tipsCard.setOnClickListener {
            showHintDialog(hint.toString())
        }
//        dynamically changing the length of numbers
        binding.numbers.filters = arrayOf(InputFilter.LengthFilter(newMaxLength))

        binding.numbers.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkAnswer()
                true
            } else {
                false
            }
        }

        setData()
        startTimer()
    }

    private fun startTimer() {
        countDownTimer?.cancel()
        binding.timeProgress.max = totalSeconds
        binding.timeProgress.progress = totalSeconds

        countDownTimer = object : CountDownTimer(totalSeconds * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                binding.timeProgress.progress = secondsLeft
                binding.time.text = "\u23F3 $secondsLeft"
            }

            override fun onFinish() {
                if (isAdded) {
                    showCustomDialog("Time's up!", "Your Score: $points\nLevel: $level")
                }
            }
        }.start()
    }

    private fun checkAnswer() {
        val userInput = binding.numbers.text.toString()
        if(userInput.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter the number", Toast.LENGTH_SHORT).show()
            return
        }

        if (userInput == actualNumber.toString()) {
            binding.movePoints.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.movePoints.text = "+20"
            binding.movePoints.visibility = View.VISIBLE
            binding.movePoints.startAnimation(moveUp)
            points += 20
            binding.points.text = points.toString()

            moveUp.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    binding.movePoints.text = ""
                    binding.movePoints.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })

            lifecycleScope.launch {
//                binding.thumbsUp.visibility = View.VISIBLE
                binding.numbers.setText("")
                delay(1000)
//                binding.thumbsUp.visibility = View.GONE
                setData()
            }

            if (points >= level * 100) {
                level++
                totalSeconds += 30
                lowRange = highRange*10
                highRange = highRange * 100
//                binding.level.text = "Level $level"
                startTimer()
                Toast.makeText(
                    requireContext(),
                    "Level $level Unlocked! +30 seconds",
                    Toast.LENGTH_LONG
                ).show()
            }

        } else {
            binding.movePoints.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.movePoints.text = "-10"
            binding.movePoints.visibility = View.VISIBLE
            binding.movePoints.startAnimation(moveDown)
            points -= 10
            binding.points.text = points.toString()

            moveDown.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    binding.movePoints.text = ""
                    binding.movePoints.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })

            showResultDialog(userInput, actualNumber.toString(), requireContext())
        }
    }

    private fun setData() {
        lifecycleScope.launch {

            actualNumber = AppFunctions.returnRandom(lowRange, highRange)
            hint = actualNumber
            binding.questionTV.visibility = View.VISIBLE
            newMaxLength = actualNumber.toString().length
            binding.numbers.filters = arrayOf(InputFilter.LengthFilter(newMaxLength))
            Log.d("Guess Number Fragment",newMaxLength.toString())
            binding.numbers.setText("")
            binding.questionTV.text = actualNumber.toString()
//            binding.questionTV.startAnimation(moveUp)

            binding.description.text = "Watch Number"
            delay(1000)
            binding.questionTV.text = ""
            binding.questionTV.visibility = View.GONE
            binding.description.text = "Enter number"
            binding.numbers.setText("")
        }
    }

    private fun showResultDialog(userAnswer: String, correctAnswer: String, context: Context) {
        val dialogView = View.inflate(context, R.layout.dialog_result, null)
        val wrongText = dialogView.findViewById<TextView>(R.id.wrongAnswerText)
        val correctText = dialogView.findViewById<TextView>(R.id.correctAnswerText)
        val okBtn = dialogView.findViewById<Button>(R.id.okButton)

        wrongText.text = "$userAnswer ❌"
        correctText.text = "$correctAnswer ✅"

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        okBtn.setOnClickListener {
            lifecycleScope.launch {
                delay(500)
                setData()
                dialog.dismiss()
            }
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun showCustomDialog(title: String, message: String) {

        if (isAdded) {


            val dialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.time_finish, null)
            val titleView = dialogView.findViewById<TextView>(R.id.dialogTitle)
            val messageView = dialogView.findViewById<TextView>(R.id.dialogMessage)
            val okButton = dialogView.findViewById<Button>(R.id.okButton)

            titleView.text = title
            messageView.text = message

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create()

            okButton.setOnClickListener {
//                AppFunctions.updateUserData(points,true,totalSeconds.toLong(),game._id!!.toInt())
                AppFunctions.updateUserDataThroughApi(points,true,totalSeconds.toLong()*1000,game._id,requireActivity())
                dialog.dismiss()
                requireActivity().onBackPressed()
            }

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.show()
        }
    }

    private fun showHintDialog(value: String) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("💡 Hint")
            .setMessage("The value is: $value")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()

        dialog.setOnShowListener {
            // Set title style
            val titleId = requireContext().resources.getIdentifier("alertTitle", "id", "android")
            val titleTextView = dialog.findViewById<TextView>(titleId)
            titleTextView?.apply {
                textSize = 20f
                setTextColor(Color.parseColor("#3F51B5")) // Indigo
                gravity = Gravity.CENTER
            }

            // Set message style
            val messageView = dialog.findViewById<TextView>(android.R.id.message)
            messageView?.apply {
                textSize = 18f
                setTextColor(Color.DKGRAY)
                gravity = Gravity.CENTER
            }

            // Optional: Set background with rounded corners manually (if needed)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        }

        dialog.show()
    }
}
