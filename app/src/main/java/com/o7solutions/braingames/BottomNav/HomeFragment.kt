package com.o7solutions.braingames.BottomNav

import android.R.id
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.o7solutions.braingames.DataClasses.Games
import com.o7solutions.braingames.Adapters.GamesAdapter
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
    var gamesList = arrayListOf<Games>()
    var problemsList = arrayListOf<Games>()
    var memoryList = arrayListOf<Games>()

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

//        Updating Streak

        binding.pgBarStreak.visibility = View.VISIBLE

        var recyclerAnimation = AnimationUtils.loadLayoutAnimation(requireContext(),R.anim.fall_down_layout)

        adapter = GamesAdapter(gamesList,this)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutAnimation = recyclerAnimation
        binding.recyclerView.scheduleLayoutAnimation()

        problemAdapter = GamesAdapter(problemsList,this)
        binding.recyclerViewProblemSolving.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewProblemSolving.adapter = problemAdapter
        binding.recyclerViewProblemSolving.layoutAnimation = recyclerAnimation
        binding.recyclerViewProblemSolving.scheduleLayoutAnimation()

        memoryAdapter = GamesAdapter(memoryList,this)
        binding.recyclerViewMemory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMemory.adapter = memoryAdapter
        binding.recyclerViewMemory.layoutAnimation = recyclerAnimation
        binding.recyclerViewMemory.scheduleLayoutAnimation()


//        (requireActivity() as BottomNavActivity).showBottomNav(true)

        getGames(AppConstants.logical,gamesList)
        getGames(AppConstants.memory,memoryList)
        fillData()
        AppFunctions.getStreak { streak->
            binding.streakTV.text = streak.count.toString()
            binding.pgBarStreak.visibility = View.GONE
//            if(binding.streakTV.text.isEmpty())
//            }
        }
    }

    fun getGames(category: Int,list: ArrayList<Games>) {
        binding.pgBar.visibility = View.VISIBLE
        db.collection(AppConstants.games)
            .whereEqualTo("category",category)
            .addSnapshotListener { snapShot,error->

            list.clear()
            if(snapShot != null) {
                for (doc in snapShot) {

                    var game = doc.toObject(Games::class.java)
                    list.add(game)
                }

                adapter.notifyDataSetChanged()
                memoryAdapter.notifyDataSetChanged()
            }

            if(error != null) {
                AppFunctions.showAlert(error.localizedMessage.toString(),requireContext())
            }

        }

        binding.pgBar.visibility = View.GONE
    }

    override fun onGameClick(game: Games) {

        val fragmentToGo = game.fragmentId
        val context = requireContext()
            val resId = context?.resources?.getIdentifier(fragmentToGo, "id", context.packageName)
        if(resId != null) {
            findNavController().navigate(resId)
        }
    }

    fun  fillData() {
//        problemsList.clear()
////        memoryList.clear()
//        problemsList.addAll(getStaticDataList())
//        memoryList.addAll(getStaticDataList())

        memoryAdapter.notifyDataSetChanged()
        problemAdapter.notifyDataSetChanged()
    }

    fun getStaticDataList(): ArrayList<Games> {
        return arrayListOf(
            Games(1, "Home", "homeFragment", "https://example.com/home", "#00FF00", 1),
            Games(2, "Profile", "profileFragment", "https://example.com/profile", "#FF5733", 1),
            Games(3, "Settings", "settingsFragment", "https://example.com/settings", "#3498DB", 1),
            Games(4, "Messages", "messagesFragment", "https://example.com/messages", "#9B59B6", 1),
            Games(5, "Notifications", "notificationsFragment", "https://example.com/notifications", "#F1C40F", 1),
            Games(6, "Search", "searchFragment", "https://example.com/search", "#1ABC9C", 1),
            Games(7, "Favorites", "favoritesFragment", "https://example.com/favorites", "#E67E22", 1),
            Games(8, "Help", "helpFragment", "https://example.com/help", "#E74C3C", 1)
        )
    }


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