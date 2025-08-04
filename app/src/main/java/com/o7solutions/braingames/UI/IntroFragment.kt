package com.o7solutions.braingames.UI

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.o7solutions.braingames.Adapters.LevelsAdapter
import com.o7solutions.braingames.DataClasses.Games
import com.o7solutions.braingames.R
import com.o7solutions.braingames.databinding.FragmentIntroBinding
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
class IntroFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentIntroBinding
    private lateinit var game: Games
    private lateinit var adapter: LevelsAdapter
    var levelsList = arrayListOf<String>()
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

        binding = FragmentIntroBinding.inflate(layoutInflater)
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

        binding.apply {
            val animationZoomOut = AnimationUtils.loadAnimation(requireActivity(), R.anim.zoom)

//            startGame.startAnimation(animationZoomOut)
            positiveScore.text = game.positiveScore
            negativeScore.text = game.negativeScore
            Glide.with(requireActivity())
                .load(game.url) // game.url must be a valid image URL
                .into(binding.imageViewGame)

            AppFunctions.getBestScore(game.id!!.toInt(),{ score->
                bestScore.text = score.toString()
            })

            levelsRecyclerView.layoutManager = LinearLayoutManager(requireActivity(),
                LinearLayoutManager.HORIZONTAL,false)
            adapter= LevelsAdapter(levelsList)
            levelsRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
//            gameName.text = game.name
            startGame.setOnClickListener {

                val bundle = Bundle().apply {
                    putSerializable("game_data", game)
                }
                val fragmentToGo = game.fragmentId
                val context = requireContext()
                val resId =
                    context?.resources?.getIdentifier(fragmentToGo, "id", context.packageName)
                if (resId != null) {
                    findNavController().navigate(resId,bundle)
                }
            }
        }
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