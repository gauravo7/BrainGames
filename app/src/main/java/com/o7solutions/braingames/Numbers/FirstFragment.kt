package com.o7solutions.braingames.Numbers

import kotlinx.coroutines.*
import android.app.AlertDialog
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
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
    var totalQuestions= 0
    var rightQuestions = 0
    var totalSeconds = 60

    lateinit var moveUp: Animation
    lateinit var moveDown: Animation
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

//        val serviceIntent = Intent(requireActivity(), UserTimerService::class.java)
//        serviceIntent.putExtra("userId", FirebaseAuth.getInstance().currentUser?.uid)
//        requireActivity().startService(serviceIntent)

        var blinkAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.blink)
        binding.viewOne.visibility = View.GONE
        binding.viewOne.startAnimation(blinkAnimation)
        binding.viewTwo.startAnimation(blinkAnimation)
//        binding.totalQuestions.startAnimation(blinkAnimation)
        binding.operator1.startAnimation(blinkAnimation)

        binding.points.text = points.toString()

        binding.apply {

            plus.setOnClickListener {

                binding.operator1.text = "+"


                if(level<3) {
                    lifecycleScope.launch {
                        checkAnswer("+")
                        delay(1000)
                        setData()
                    }
                } else {

//                    lifecycleScope.

                }



            }

            minus.setOnClickListener {
                binding.operator1.text = "-"
                if(level<3) {
                    lifecycleScope.launch {
                        checkAnswer("+")
                        delay(1000)
                        setData()
                    }
                } else {

                }
            }

            division.setOnClickListener {

                binding.operator1.text = "\u00F7"

                if(level<3) {
                    lifecycleScope.launch {
                        checkAnswer("+")
                        delay(1000)
                        setData()
                    }
                } else {

                }
            }

            multiply.setOnClickListener {

                binding.operator1.text = "x"
                if(level<3) {
                    lifecycleScope.launch {
                        checkAnswer("+")
                        delay(1000)
                        setData()
                    }
                } else {

                }

            }
        }
        setData()
        startTimer()
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

//    fun clear() {
//
//
//        operator = ""
//        operator2 = ""
//        binding.operator2.setText("")
//        binding.operator1.setText("")
//        binding.operand1.text = ""
//        binding.operand2.text = ""
//        binding.operand3.text = ""
//        binding.answer.text = ""
//        Log.d("First Fragment","clear called ${binding.operator1.text}")
//        Log.d("First Fragment","$operator")
//    }

    fun updateQuestions() {
        binding.totalQuestions.text = "$rightQuestions / $totalQuestions"
    }

    private fun checkAnswer(choice: String) {


        if(choice == "/") {
            binding.operator1.text = "\u00F7"
        }
        else {
            binding.operator1.text = choice
        }


        if (choice == operator) {
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

        }
        else {
            binding.questionCard.setStrokeColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )

            binding.movePoints.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            binding.movePoints.text = "-10"
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

            var correctAnswer = "${operand1} ${operator} ${operand2} = ${answer}"
            var wrongAnswer = "${operand1} ${choice} ${operand2} = ${answer}"
            showResultDialog(wrongAnswer, correctAnswer)

        }

        if(points >= 200) {
            level = 2
            totalSeconds = totalSeconds+60
        } else if(points >= 400) {
            level = 3
            totalSeconds = totalSeconds+60
        } else if(points >= 600) {
            level = 4
            totalSeconds = totalSeconds+60
        }



    }

    private fun setData() {
        totalQuestions++
        updateQuestions()
        questionList = LevelOne.solveLevelOne()
        operand1 = questionList[0]
        operand2 = questionList[1]
        operator = questionList[2]
        answer = questionList[3]

        binding.questionCard.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.white))

        binding.operator1.text = "?"
        binding.operator2.text = "?"
        binding.operand1.text = "${operand1}"
        binding.operand2.text = "$operand2"
        binding.answer.text = "$answer"

    }

    private fun setDataLevel3() {
        questionList = LevelThree.solveLevelThree()
        operand1 = questionList[0]
        operand2 = questionList[1]
        operand3 = questionList[2]

        operator = questionList[3]
        operator2 = questionList[4]
        answer = questionList[5]

        binding.viewTwo.visibility = View.VISIBLE
        binding.operator2.visibility = View.VISIBLE
        binding.operand3.visibility = View.VISIBLE
        binding.questionCard.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.white))

        binding.operand1.text = "${operand1}"
        binding.operator1.text = ""
        binding.operator2.text = ""
        binding.operand2.text = "$operand2"
        binding.operand3.text = "$operand3"
        binding.answer.text = "$answer"

    }

    private fun checkAnswerHigherLevel(op1: String,op2: String) {


        var actualAnswer = "$operand1$operator$operand2$operator2$operand3=$answer"
        var userAnswer =  "${binding.operand1.text.toString()}$op1${binding.operand2.text.toString()}$op2=${binding.answer.text.toString()}"

        if(actualAnswer == userAnswer) {
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

//        val imageView = dialogView.findViewById<ImageView>(R.id.dialogImage)

        var winingMessage = ""
        if(points > 200) {
            winingMessage = "You won \uD83D\uDE00 "
//            AppFunctions.updateUserData(points,true)
        } else {
            winingMessage = "You lose \uD83D\uDE22"
//            AppFunctions.updateUserData(points,false)

        }

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade)
        val titleView = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val messageView = dialogView.findViewById<TextView>(R.id.dialogMessage)
        val okButton = dialogView.findViewById<Button>(R.id.okButton)
//        imageView.startAnimation(animation)
        titleView.startAnimation(animation)
        titleView.text = "\u23F3 Time Up"
        messageView.text ="$rightQuestions/$totalQuestions \n$message\n$winingMessage"

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        okButton.setOnClickListener {

            if(points < 200) {
                AppFunctions.updateUserData(points,false,60000)
            } else {
                AppFunctions.updateUserData(points,true,60000)
            }
            dialog.dismiss()

//            val stopIntent = Intent(requireActivity(), UserTimerService::class.java)
//            requireActivity().stopService(stopIntent)

            findNavController().popBackStack()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }
}