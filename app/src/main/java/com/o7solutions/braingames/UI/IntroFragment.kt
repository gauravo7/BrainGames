package com.o7solutions.braingames.UI

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.o7solutions.braingames.Adapters.LevelsAdapter
import com.o7solutions.braingames.Adapters.OnLevelClickListener
import com.o7solutions.braingames.DataClasses.Auth.UserResponse
import com.o7solutions.braingames.DataClasses.GameFetchData
import com.o7solutions.braingames.DataClasses.Games
import com.o7solutions.braingames.R
import com.o7solutions.braingames.databinding.FragmentIntroBinding
import com.o7solutions.braingames.utils.AppConstants
import com.o7solutions.braingames.utils.AppFunctions

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [IntroFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IntroFragment : Fragment(), OnLevelClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentIntroBinding
    private lateinit var game: GameFetchData.Data
    private lateinit var adapter: LevelsAdapter
    var levelsList = arrayListOf<String>()
    var bestScored = 0
    var level = 1
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

        binding = FragmentIntroBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val dialogView = LayoutInflater.from(requireContext())
                        .inflate(R.layout.custom_exit_dialog, null)

                    val dialog = AlertDialog.Builder(requireContext())
                        .setView(dialogView)
                        .setCancelable(false)
                        .create()

                    dialogView.findViewById<Button>(R.id.btnYes).setOnClickListener {
                        dialog.dismiss()
                        findNavController().popBackStack()
                    }

                    dialogView.findViewById<Button>(R.id.btnNo).setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show()
                }
            }
        )

        getBestScores()
        binding.apply {
            val animationZoomOut = AnimationUtils.loadAnimation(requireActivity(), R.anim.zoom)

//            startGame.startAnimation(animationZoomOut)
            positiveScore.text = game.positiveScore.toString()
            negativeScore.text = game.negativeScore.toString()
            description.text = game.description.toString()
            Glide.with(requireActivity())
                .load(AppConstants.imageAddress + game.image) // game.url must be a valid image URL
                .into(binding.imageViewGame)

//            AppFunctions.getBestScore(game._id!!.toInt(),{ score->
//                bestScore.text = score.toString()
//            })

            levelsRecyclerView.layoutManager = LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.HORIZONTAL, false
            )
            var unlocked = (bestScored / 200)
            Log.d("Unlocked",unlocked.toString())
            adapter = LevelsAdapter(levelsList, game.maxLevels,unlocked,this@IntroFragment)
            levelsRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
//            gameName.text = game.name
            startGame.setOnClickListener {

                val bundle = Bundle().apply {
                    putSerializable("game_data", game)
                    putInt("level",level)
                }

                Log.d("Level",level.toString())


                val fragmentToGo = game.fragmentId
                val context = requireContext()
                val resId = context.resources?.getIdentifier(fragmentToGo, "id", context.packageName)

                resId?.let { destinationId ->
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.introFragment, true) // clear backstack
                        .build()

                    findNavController().navigate(destinationId, bundle, navOptions)
                }

            }
        }
    }

    fun getBestScores() {
        var list = arrayListOf<UserResponse.GameHistory>()
        var user = AppFunctions.getUser(requireContext())
        if (user != null) {
            list.addAll(user.gameHistory)
        }

        for (i in list) {
            if (i.gameId == game._id) {
                if ((i.bestScore ?: 0) > 0) {

                    bestScored = i.bestScore
                    binding.bestScore.text = i.bestScore.toString()
                } else {
                    binding.bestScore.text = "0"
                    bestScored = 0
                }
                Log.d("Best Score", i.bestScore.toString())
            }
        }
    }

    override fun onLevelClicked(levelNumber: Int) {
//        Toast.makeText(requireActivity(), "${levelNumber}", Toast.LENGTH_SHORT).show()
        level = levelNumber
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment IntroFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            IntroFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}