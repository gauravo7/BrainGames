package com.o7solutions.braingames.OddOut

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
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
    private lateinit var adapter : OddOutAdapter

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
        binding = FragmentOddOutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
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

    private fun setupRecyclerView() {
        // Example drawable resources
//        val images = listOf(
//            R.drawable.rectangle, R.drawable.circle, R.drawable.star,
//            R.drawable.circle, R.drawable.rectangle, R.drawable.star
//        )
//        val answerIndex = 1 // Assume the odd one is at index 1
//        val itemCount = images.size

        var listOfIndexes = GameLib.getUniqueRandomNumbers(0,16,7)
        var answerIndex = GameLib.getRandomNumberFromList(listOfIndexes)
        val images = listOf(
            R.drawable.rectangle, R.drawable.circle, R.drawable.star,R.drawable.cone
        )
        var answerIndexImage = AppFunctions.returnRandom(0,4)
        Log.d("Answer Index",answerIndexImage.toString())

        adapter = OddOutAdapter(listOfIndexes, answerIndex, 16,answerIndexImage, object : OddOutAdapter.OnClick {
            override fun onImageClick(isCorrect: Boolean) {
                Toast.makeText(requireContext(), if (isCorrect) "Correct!" else "Wrong!", Toast.LENGTH_SHORT).show()
                setupRecyclerView()
            }
        })
        Toast.makeText(requireContext(), answerIndex.toString(), Toast.LENGTH_SHORT).show()

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 4) // 3 columns
        binding.recyclerView.adapter = adapter
    }

}