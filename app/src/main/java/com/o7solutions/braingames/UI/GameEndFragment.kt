package com.o7solutions.braingames.UI

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.o7solutions.braingames.DataClasses.Auth.UserResponse.ScoreHistory
import com.o7solutions.braingames.R
import com.o7solutions.braingames.databinding.FragmentGameEndBinding
import com.github.mikephil.charting.data.Entry
import com.o7solutions.braingames.DataClasses.Auth.UserResponse
import com.o7solutions.braingames.utils.AppFunctions

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GameEndFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameEndFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentGameEndBinding
    var id= ""
    var score = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            id = it.getString("id").toString()
            score = it.getString("score").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_game_end, container, false)
        binding = FragmentGameEndBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = AppFunctions.getUser(requireActivity())

        val scoreHistoryList = user?.gameHistory
            ?.find { it.gameId == id.toString() }
            ?.scoreHistory ?: emptyList()

        setupLineChart(scoreHistoryList, view.findViewById(R.id.lineChart))

        binding.gameScore.text = score

        binding.nextButton.setOnClickListener {


            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }
    fun setupLineChart(scoreList: List<UserResponse.ScoreHistory>, chart: LineChart) {
        val entries = ArrayList<Entry>()

        // Sort by date
        val sortedList = scoreList.sortedBy { it.date }

        // Create entries
        sortedList.forEachIndexed { index, scoreHistory ->
            entries.add(Entry(index.toFloat(), scoreHistory.score.toFloat()))
        }

        // Add new entry with label "Now"
        val newIndex = sortedList.size
        entries.add(Entry(newIndex.toFloat(), score.toFloat()))

        // Labels
        val labels = sortedList.map { it.date.substring(0, 10) }.toMutableList()
        labels.add("Now")

        val lineDataSet = LineDataSet(entries, "Scores Over Time")
        lineDataSet.color = Color.BLUE
        lineDataSet.circleRadius = 5f
        lineDataSet.setDrawValues(false)

        val lineData = LineData(lineDataSet)
        chart.data = lineData

        // Chart appearance
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)
        chart.animateX(1000)

        // X-Axis formatting
        val xAxis = chart.xAxis
        xAxis.granularity = 1f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.labelRotationAngle = -45f

        chart.invalidate()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GameEndFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GameEndFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    
}