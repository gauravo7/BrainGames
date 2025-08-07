package com.o7solutions.braingames.Numbers

import kotlinx.coroutines.*
import android.app.AlertDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.o7solutions.braingames.DataClasses.GameFetchData
import com.o7solutions.braingames.DataClasses.Games
import com.o7solutions.braingames.R
import com.o7solutions.braingames.R.layout.dialog_result
import com.o7solutions.braingames.databinding.FragmentFirstBinding
import com.o7solutions.braingames.utils.AppFunctions

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private lateinit var binding: FragmentFirstBinding
    var db = FirebaseFirestore.getInstance()
    var index = 1
    var operand1 = " "
    var operand2 = " "
    var operand3 = " "
    var operator = ""
    var operator2 = " "
    var answer = ""
    var points = 0
    var level = 1
    var questionList = arrayListOf<String>()
    private var countDownTimer: CountDownTimer? = null
    var totalQuestions = 0
    var rightQuestions = 0
    var totalSeconds = 60

    // For level 3+ tracking
    var selectedOperator1 = ""
    var selectedOperator2 = ""
    var isFirstOperatorSelected = false

    lateinit var moveUp: Animation
    lateinit var moveDown: Animation
    private lateinit var game: GameFetchData.Data

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

        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        moveUp = AnimationUtils.loadAnimation(requireContext(), R.anim.move_up)
        moveDown = AnimationUtils.loadAnimation(requireContext(), R.anim.move_down)

        var blinkAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.blink)

        binding.operator1.startAnimation(blinkAnimation)
        binding.operator2.startAnimation(blinkAnimation)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
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

//
        binding.points.text = points.toString()


        binding.tipsCard.setOnClickListener {
            performTipsFunctionality()
        }

        binding.apply {

            plus.setOnClickListener {
                handleOperatorClick("+")
                binding.plus.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.divide_button_bg)
                )
            }

            minus.setOnClickListener {
                handleOperatorClick("-")
                binding.minus.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.divide_button_bg)
                )
            }

            division.setOnClickListener {
                handleOperatorClick("/")
                binding.division.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.divide_button_bg)
                )
            }

            multiply.setOnClickListener {
                handleOperatorClick("x")
                binding.multiply.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.divide_button_bg)
                )
            }
        }
        setData()
        startTimer()
    }

    private fun handleOperatorClick(operatorChoice: String) {
        if (level < 3) {
            // Level 1-2: Single operator guess
            binding.operator1.text =
                if (operatorChoice == "/") "\u00F7" else if (operatorChoice == "*") "x" else operatorChoice

            lifecycleScope.launch {
                checkAnswer(operatorChoice)
                delay(1000)
                setData()
            }
        } else {
            if (!isFirstOperatorSelected) {
                selectedOperator1 = operatorChoice
                binding.operator1.text =
                    if (operatorChoice == "/") "\u00F7" else if (operatorChoice == "*") "x" else operatorChoice
                isFirstOperatorSelected = true
                Toast.makeText(
                    requireContext(),
                    "Now select the second operator",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Second operator selection
                selectedOperator2 = operatorChoice
                binding.operator2.text =
                    if (operatorChoice == "/") "\u00F7" else if (operatorChoice == "*") "x" else operatorChoice

                lifecycleScope.launch {
                    checkAnswerHigherLevel(selectedOperator1, selectedOperator2)
                    delay(1000)
                    setDataLevel3()
                    isFirstOperatorSelected = false
                    selectedOperator1 = ""
                    selectedOperator2 = ""
                }
            }
        }
    }

//    fun performTipsFunctionality() {
//
//        if (operator == "+") {
//
//            binding.plus.setCardBackgroundColor(
//                ContextCompat.getColor(requireContext(), R.color.green)
//            )
//
//        }
//        else if (operator == "-") {
//
//            binding.minus.setCardBackgroundColor(
//                ContextCompat.getColor(requireContext(), R.color.green)
//            )
////            binding.minus.setStrokeColor(
////                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.green))
////            )
//        }
//        else if (operator == "x") {
//
//            binding.multiply.setCardBackgroundColor(
//                ContextCompat.getColor(requireContext(), R.color.green)
//            )
//
//        } else if (operator == "/") {
//
//            binding.division.setCardBackgroundColor(
//                ContextCompat.getColor(requireContext(), R.color.green)
//            )
//
//        }
////        binding.operator1.text = " "
////        binding.operator2.text = " "
////        binding.operand1.text = " "
////        binding.operand2.text = " "
////        binding.operand3.text = " "
////        binding.answer.text = " "
//////        binding.equalTo.text = " "
////
////        Toast.makeText(requireContext(), "Question solved!", Toast.LENGTH_SHORT).show()
//////        if(level < 3) {
////            rightQuestions++
////            updateQuestions()
//////            Toast.makeText(requireContext(), "Correct answer!", Toast.LENGTH_SHORT).show()
////            index++
////            binding.questionCard.setStrokeColor(
////                ContextCompat.getColor(
////                    requireContext(),
////                    R.color.green
////                )
////            )
////
////            binding.movePoints.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
////            binding.movePoints.text = "+20"
////            binding.movePoints.visibility = View.VISIBLE
////            binding.movePoints.startAnimation(moveUp)
////            points = points + 20
////            binding.points.text = points.toString()
////            moveUp.setAnimationListener(object : Animation.AnimationListener {
////                override fun onAnimationStart(animation: Animation?) {}
////
////                override fun onAnimationEnd(animation: Animation?) {
////                    binding.movePoints.text = ""
////                    binding.movePoints.visibility = View.INVISIBLE
////                }
////
////                override fun onAnimationRepeat(animation: Animation?) {}
////            })
////            setData()
////
////
////        if(points >= 200 && level == 1) {
////            level = 2
////            totalSeconds = totalSeconds + 60
////            startTimer()
////            Toast.makeText(requireContext(), "Level 2 Unlocked! +60 seconds", Toast.LENGTH_LONG).show()
////        } else if(points >= 400 && level == 2) {
////            level = 3
////            totalSeconds = totalSeconds + 60
////            startTimer()
////            Toast.makeText(requireContext(), "Level 3 Unlocked! Now guess both operators! +60 seconds", Toast.LENGTH_LONG).show()
////        } else if(points >= 600 && level == 3) {
////            level = 4
////            totalSeconds = totalSeconds + 60
////            startTimer()
////            Toast.makeText(requireContext(), "Level 4 Unlocked! +60 seconds", Toast.LENGTH_LONG).show()
////        }
//
//    }

    fun performTipsFunctionality() {
        if (level < 3) {
            // Levels 1 and 2: Single operator
            when (operator) {
                "+" -> binding.plus.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                "-" -> binding.minus.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                "x" -> binding.multiply.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                "/" -> binding.division.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
            }
        } else {
            // Levels 3 and 4: Two operators
            when (operator) {
                "+" -> binding.plus.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                "-" -> binding.minus.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                "x" -> binding.multiply.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                "/" -> binding.division.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
            }

            when (operator2) {
                "+" -> binding.plus.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                "-" -> binding.minus.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                "x" -> binding.multiply.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                "/" -> binding.division.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
            }
        }
    }

    private fun startTimer() {
        countDownTimer?.cancel()

        binding.seekBarBrightness.max = totalSeconds
        binding.seekBarBrightness.progress = totalSeconds

        countDownTimer = object : CountDownTimer(totalSeconds * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                binding.seekBarBrightness.progress = secondsLeft
                binding.time.text = "\u23F3 $secondsLeft"
            }

            override fun onFinish() {
                if (!isAdded) return
                binding.seekBarBrightness.progress = 0
                Toast.makeText(requireActivity(), "Time's up!", Toast.LENGTH_SHORT).show()
                showCustomDialog("Time Up", "Total Points=${points}")
            }
        }.start()
    }

    fun updateQuestions() {
        binding.totalQuestions.text = "$rightQuestions / $totalQuestions"
    }

    private fun checkAnswer(choice: String) {
        var displayChoice = choice

        var calculatedAnswer = 0
        if (choice == "/") {
            displayChoice = "\u00F7"
            calculatedAnswer = operand1.toInt() / operand2.toInt()
        } else if (choice == "x") {
            displayChoice = "x"
            calculatedAnswer = operand1.toInt() * operand2.toInt()
        } else if (choice == "+") {
            calculatedAnswer = operand1.toInt() + operand2.toInt()
        } else if (choice == "-") {
            calculatedAnswer = operand1.toInt() - operand2.toInt()

        }
//        var userChoice = "${operand1} ${if(operator == "/") "\u00F7" else if(operator == "*") "x" else operator} ${operand2} = ${answer}"
//
//        var actualAnswer = "${operand1} ${displayChoice} ${operand2} = ${answer}"
        binding.operator1.text = displayChoice

        if (calculatedAnswer.toString() == answer) {
            rightQuestions++
            updateQuestions()
            Toast.makeText(requireContext(), "Correct answer!", Toast.LENGTH_SHORT).show()
            index++
            binding.questionCard.setStrokeColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green
                )
            )

            binding.movePoints.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            binding.movePoints.text = "+20"
            binding.movePoints.visibility = View.VISIBLE
            binding.movePoints.startAnimation(moveUp)
            points = points + 20
            binding.points.text = points.toString()
            moveUp.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    binding.movePoints.text = ""
                    binding.movePoints.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })

        } else {
            binding.questionCard.setStrokeColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )

            binding.movePoints.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            binding.movePoints.text = "-10"
            binding.movePoints.visibility = View.VISIBLE
            binding.movePoints.startAnimation(moveDown)
            points = points - 10
            binding.points.text = points.toString()
            moveDown.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    binding.movePoints.text = ""
                    binding.movePoints.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })

            var correctAnswer =
                "${operand1} ${if (operator == "/") "\u00F7" else if (operator == "*") "x" else operator} ${operand2} = ${answer}"
            var wrongAnswer = "${operand1} ${displayChoice} ${operand2} = ${answer}"
            showResultDialog(wrongAnswer, correctAnswer)
        }

        if (points >= 200 && level == 1) {
            level = 2
            totalSeconds = totalSeconds + 60
            startTimer()
            Toast.makeText(requireContext(), "Level 2 Unlocked! +60 seconds", Toast.LENGTH_LONG)
                .show()
        } else if (points >= 400 && level == 2) {
            level = 3
            totalSeconds = totalSeconds + 60
            startTimer()
            Toast.makeText(
                requireContext(),
                "Level 3 Unlocked! Now guess both operators! +60 seconds",
                Toast.LENGTH_LONG
            ).show()
        } else if (points >= 600 && level == 3) {
            level = 4
            totalSeconds = totalSeconds + 60
            startTimer()
            Toast.makeText(requireContext(), "Level 4 Unlocked! +60 seconds", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun setData() {
        totalQuestions++
        updateQuestions()

        if (level < 3) {
            // Level 1-2: Single operator format
            if (level < 2) {
                questionList = LevelOne.solveLevelOne()
            } else if (level > 1) {
                questionList = LevelTwo.solveLevelTwo()

            }

            operand1 = questionList[0]
            operand2 = questionList[1]
            operator = questionList[2]
            answer = questionList[3]

            // Hide level 3+ UI elements
//            binding.viewTwo.visibility = View.GONE
            binding.operator2.visibility = View.GONE
            binding.operand3.visibility = View.GONE

            binding.questionCard.setStrokeColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )

            binding.operator1.text = "?"
            binding.operand1.text = operand1
            binding.operand2.text = operand2
            binding.answer.text = answer
        } else {
            // Level 3+: Switch to two operator format
            setDataLevel3()
        }
    }

    private fun setDataLevel3() {
        totalQuestions++
        updateQuestions()

        if (level > 3) {
            questionList = LevelFour.solveLevelFour()

        } else if (level < 4) {
            questionList = LevelThree.solveLevelThree()

        }
        operand1 = questionList[0]
        operand2 = questionList[1]
        operand3 = questionList[2]
        operator = questionList[3]
        operator2 = questionList[4]
        answer = questionList[5]

        // Show level 3+ UI elements
//        binding.viewTwo.visibility = View.VISIBLE
        binding.operator2.visibility = View.VISIBLE
        binding.operand3.visibility = View.VISIBLE

        binding.questionCard.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.white))

        binding.operand1.text = operand1
        binding.operator1.text = "?"
        binding.operator2.text = "?"
        binding.operand2.text = operand2
        binding.operand3.text = operand3
        binding.answer.text = answer
    }

    private fun checkAnswerHigherLevel(op1: String, op2: String) {
        // Convert display operators back to actual operators for comparison
        val actualOp1 = when (operator) {
            "/" -> "/"
            "*" -> "*"
            "+" -> "+"
            "-" -> "-"
            else -> operator
        }

        val actualOp2 = when (operator2) {
            "/" -> "/"
            "*" -> "*"
            "+" -> "+"
            "-" -> "-"
            else -> operator2
        }

        if (op1 == actualOp1 && op2 == actualOp2) {
            rightQuestions++
            updateQuestions()
            Toast.makeText(requireContext(), "Correct answer!", Toast.LENGTH_SHORT).show()
            index++
            binding.questionCard.setStrokeColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green
                )
            )

            binding.movePoints.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            binding.movePoints.text = "+20"
            binding.movePoints.visibility = View.VISIBLE
            binding.movePoints.startAnimation(moveUp)
            points = points + 20
            binding.points.text = points.toString()
            moveUp.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    binding.movePoints.text = ""
                    binding.movePoints.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
        } else {
            binding.questionCard.setStrokeColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )

            binding.movePoints.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            binding.movePoints.text = "-10"
            binding.movePoints.visibility = View.VISIBLE
            binding.movePoints.startAnimation(moveDown)
            points = points - 10
            binding.points.text = points.toString()
            moveDown.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    binding.movePoints.text = ""
                    binding.movePoints.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })

            val correctAnswer =
                "${operand1} ${if (operator == "/") "\u00F7" else if (operator == "*") "x" else operator} ${operand2} ${if (operator2 == "/") "\u00F7" else if (operator2 == "*") "x" else operator2} ${operand3} = ${answer}"
            val wrongAnswer =
                "${operand1} ${if (op1 == "/") "\u00F7" else if (op1 == "*") "x" else op1} ${operand2} ${if (op2 == "/") "\u00F7" else if (op2 == "*") "x" else op2} ${operand3} = ${answer}"
            showResultDialog(wrongAnswer, correctAnswer)
        }

        // Level progression logic for higher levels
        if (points >= 600 && level == 3) {
            level = 4
            totalSeconds = totalSeconds + 60
            startTimer() // Restart timer with new time
            Toast.makeText(requireContext(), "Level 4 Unlocked! +60 seconds", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun showResultDialog(userAnswer: String, correctAnswer: String) {
        val dialogView = layoutInflater.inflate(dialog_result, null)

        val wrongText = dialogView.findViewById<TextView>(R.id.wrongAnswerText)
        val correctText = dialogView.findViewById<TextView>(R.id.correctAnswerText)
        val okBtn = dialogView.findViewById<Button>(R.id.okButton)

        wrongText.text = "Your Answer: $userAnswer❌"
        correctText.text = "Correct Answer: $correctAnswer✅"

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        okBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun showCustomDialog(title: String, message: String) {
        val dialogView = layoutInflater.inflate(R.layout.time_finish, null)

        var winingMessage = ""
        if (points > 200) {
            winingMessage = "You won \uD83D\uDE00 "
        } else {
            winingMessage = "You lose \uD83D\uDE22"
        }

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade)
        val titleView = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val messageView = dialogView.findViewById<TextView>(R.id.dialogMessage)
        val okButton = dialogView.findViewById<Button>(R.id.okButton)

        titleView.startAnimation(animation)
        titleView.text = "\u23F3 Time Up"
        messageView.text = "$rightQuestions/$totalQuestions \n$message\n$winingMessage"

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        okButton.setOnClickListener {
            if (points < 200) {

                AppFunctions.updateUserDataThroughApi(
                    points,
                    false,
                    totalSeconds.toLong() * 1000,
                    game._id.toString(),
                    requireContext()
                )

//                AppFunctions.updateUserData(points,false,60000,game._id!!.toInt())
            } else {
                AppFunctions.updateUserDataThroughApi(
                    points,
                    false,
                    totalSeconds.toLong() * 1000,
                    game._id.toString(),
                    requireContext()
                )
            }
            dialog.dismiss()
            findNavController().popBackStack()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }
}