package com.o7solutions.braingames.GuessNumber

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.o7solutions.braingames.R
import com.o7solutions.braingames.databinding.FragmentGuessNumberBinding
import com.o7solutions.braingames.databinding.FragmentWordGameBinding
import com.o7solutions.braingames.utils.AppFunctions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GuessNumberFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GuessNumberFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var points = 0
    var level = 1
    var numberList = arrayListOf<String>()
    var lowRange = 100
    var highRange = 1000
    var actualNumber = 0
    private lateinit var binding: FragmentGuessNumberBinding
    lateinit var moveUp: Animation
    lateinit var moveDown: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGuessNumberBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.points.text = "0"
        moveUp = AnimationUtils.loadAnimation(requireContext(), R.anim.move_up)
        moveDown = AnimationUtils.loadAnimation(requireContext(), R.anim.move_down)
        binding.numbers.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkAnswer()
                true
            } else {
                false
            }
        }
        setData()
    }

    fun checkAnswer() {


        if(actualNumber.toString() == binding.numbers.text.toString()) {
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
            lifecycleScope.launch {
                binding.thumbsUp.visibility = View.VISIBLE
                binding.numbers.setText("")
                delay(1000)
                binding.thumbsUp.visibility = View.GONE
                setData()
            }

        }
        else {

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
            showResultDialog(binding.numbers.text.toString(),actualNumber.toString(),requireActivity())


        }

    }


    fun setData() {

        lifecycleScope.launch {
            actualNumber = AppFunctions.returnRandom(lowRange,highRange)
            binding.questionTV.visibility = View.VISIBLE
            binding.numbers.setText("")
            binding.questionTV.setText(actualNumber.toString())
            binding.questionTV.startAnimation(moveUp)

            binding.description.text = "Watch Number"
            delay(1000)
            binding.questionTV.text = ""
            binding.questionTV.visibility = View.GONE
            binding.numbers.setText("")
            binding.description.text = "Enter number"

        }

    }

    fun showResultDialog(userAnswer: String, correctAnswer: String,context: Context) {
        val dialogView = View.inflate(context,R.layout.dialog_result, null)

        val wrongText = dialogView.findViewById<TextView>(R.id.wrongAnswerText)
        val correctText = dialogView.findViewById<TextView>(R.id.correctAnswerText)
        val okBtn = dialogView.findViewById<Button>(R.id.okButton)

        wrongText.text = "$userAnswer❌"
        correctText.text = "$correctAnswer✅"

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
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GuessNumberFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GuessNumberFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

//binding.apply {
//    btn1.setOnClickListener {
//        numberList.add("1")
//    }
//    btn2.setOnClickListener {
//        numberList.add("2")
//    }
//    btn3.setOnClickListener {
//        numberList.add("3")
//    }
//    btn4.setOnClickListener {
//        numberList.add("4")
//    }
//    btn5.setOnClickListener {
//        numberList.add("5")
//    }
//    btn6.setOnClickListener {
//        numberList.add("6")
//    }
//    btn7.setOnClickListener {
//        numberList.add("7")
//    }
//    btn8.setOnClickListener {
//        numberList.add("8")
//    }
//    btn9.setOnClickListener {
//        numberList.add("9")
//    }
//}
