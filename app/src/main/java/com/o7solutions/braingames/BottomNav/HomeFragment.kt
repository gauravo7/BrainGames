package com.o7solutions.braingames.BottomNav

import android.R.id
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.o7solutions.braingames.DataClasses.Games
import com.o7solutions.braingames.Adapters.GamesAdapter
import com.o7solutions.braingames.BottomNav.ViewModel.HomeViewModel
import com.o7solutions.braingames.DataClasses.GameFetchData
import com.o7solutions.braingames.Model.Repository
import com.o7solutions.braingames.Model.RetrofitClient
import com.o7solutions.braingames.Model.StateClass
import com.o7solutions.braingames.R
import com.o7solutions.braingames.databinding.FragmentHomeBinding
import com.o7solutions.braingames.utils.AppConstants
import com.o7solutions.braingames.utils.AppFunctions

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), GamesAdapter.OnClick {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentHomeBinding
    lateinit var db : FirebaseFirestore
    var gamesList = arrayListOf<GameFetchData.Data>()
    var problemsList = arrayListOf<GameFetchData.Data>()
    var memoryList = arrayListOf<GameFetchData.Data>()
    var logicalList = arrayListOf<GameFetchData.Data>()
    val viewModel: HomeViewModel by lazy {
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = Repository(RetrofitClient.authInstance) // Use your singleton Repository instance
                return HomeViewModel(repo) as T // <-- pass repo instead of apiService
            }
        }
        ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }


    private lateinit var adapter: GamesAdapter
    private lateinit var problemAdapter : GamesAdapter
    private lateinit var memoryAdapter : GamesAdapter

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

        db = FirebaseFirestore.getInstance()
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        viewModel.getGames()
//        Updating Streak
//        binding.pgBarStreak.visibility = View.VISIBLE

        var recyclerAnimation = AnimationUtils.loadLayoutAnimation(requireContext(),R.anim.fall_down_layout)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }


//        val user = AppFunctions.getUser(requireActivity())
//        if (user != null) {
//            binding.streakTV.text = user.streak.count.toString()
//        }
//        Logical games

        Log.d("Token", AppFunctions.getToken(requireContext()).toString())
        adapter = GamesAdapter(logicalList,this)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(),2)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutAnimation = recyclerAnimation
        binding.recyclerView.scheduleLayoutAnimation()
        adapter.notifyDataSetChanged()


//        problem solving games
        problemAdapter = GamesAdapter(problemsList,this)
        binding.recyclerViewProblemSolving.layoutManager = GridLayoutManager(requireContext(),2)
        binding.recyclerViewProblemSolving.adapter = problemAdapter
        binding.recyclerViewProblemSolving.layoutAnimation = recyclerAnimation
        binding.recyclerViewProblemSolving.scheduleLayoutAnimation()
//


//        Memory based games
        memoryAdapter = GamesAdapter(memoryList,this)
        binding.recyclerViewMemory.layoutManager = GridLayoutManager(requireContext(),2)
        binding.recyclerViewMemory.adapter = memoryAdapter
        binding.recyclerViewMemory.layoutAnimation = recyclerAnimation
        binding.recyclerViewMemory.scheduleLayoutAnimation()

//
////        (requireActivity() as BottomNavActivity).showBottomNav(true)
//
//        getGames(AppConstants.logical,gamesList)
//        getGames(AppConstants.memory,memoryList)
//        getGames(AppConstants.problemSolving,problemsList)
//        fillData()
//        AppFunctions.getStreak { streak->
//            binding.streakTV.text = streak.count.toString()
//            binding.pgBarStreak.visibility = View.GONE
////            if(binding.streakTV.text.isEmpty())
////            }
//        }
    }

    fun initViews() {
        viewModel.gameState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is StateClass.Loading -> {
                    // Show loading UI
                    binding.pgBar.visibility = View.VISIBLE
                }
                is StateClass.Success -> {
                    binding.pgBar.visibility = View.GONE

                    gamesList.clear()
                    val games = state.data
                    gamesList.addAll(games)
                    Log.d("Games List",gamesList.toString())
                    segregate()
//                    adapter.notifyDataSetChanged()
                    // Display in RecyclerView
                }
                is StateClass.Error -> {
                    binding.pgBar.visibility = View.GONE

                    Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_SHORT).show()
                }


            }


        }
    }


    fun segregate() {
        logicalList.clear()
        memoryList.clear()
        problemsList.clear()

        for(game in gamesList) {

            if(game.gameCategoryId == AppConstants.logical) {
                logicalList.add(game)
            } else if(game.gameCategoryId == AppConstants.memory) {
                memoryList.add(game)
            } else if(game.gameCategoryId == AppConstants.problemSolving) {
                problemsList.add(game)
            }
        }
        adapter.notifyDataSetChanged()
        problemAdapter.notifyDataSetChanged()
        memoryAdapter.notifyDataSetChanged()
    }

    override fun onGameClick(game: GameFetchData.Data) {
        val bundle = Bundle().apply {
            putSerializable("game_data", game)
        }
        findNavController().navigate(R.id.introFragment, bundle)
//        val fragmentToGo = game.fragmentId
//        val context = requireContext()
//        val resId = context?.resources?.getIdentifier(fragmentToGo, "id", context.packageName)
//        if(resId != null) {
//            findNavController().navigate(resId)
//        }
    }
//    fun getGames(category: Int,list: ArrayList<Games>) {
//        binding.pgBar.visibility = View.VISIBLE
//        db.collection(AppConstants.games)
//            .whereEqualTo("category",category)
//            .addSnapshotListener { snapShot,error->
//
//            list.clear()
//            if(snapShot != null) {
//                for (doc in snapShot) {
//
//                    var game = doc.toObject(Games::class.java)
//                    list.add(game)
//                }
//
////                if(category == AppConstants.logical) {
////                    adapter.notifyDataSetChanged()
////                } else if(category == AppConstants.memory) {
////                    memoryAdapter.notifyDataSetChanged()
////
////                } else if(category == AppConstants.problemSolving) {
////                    problemAdapter.notifyDataSetChanged()
////                }
//            }
//
//            if(error != null) {
//                AppFunctions.showAlert(error.localizedMessage.toString(),requireContext())
//            }
//
//        }
//
//        binding.pgBar.visibility = View.GONE
//    }



//    fun  fillData() {
////        problemsList.clear()
//////        memoryList.clear()
////        problemsList.addAll(getStaticDataList())
////        memoryList.addAll(getStaticDataList())
//
//        memoryAdapter.notifyDataSetChanged()
//        problemAdapter.notifyDataSetChanged()
//    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}