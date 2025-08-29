package com.o7solutions.braingames.BottomNav

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.o7solutions.braingames.BottomNav.ViewModel.ProfileViewModel
import com.o7solutions.braingames.DataClasses.Auth.UserResponse
import com.o7solutions.braingames.Model.Repository
import com.o7solutions.braingames.Model.RetrofitClient
import com.o7solutions.braingames.Model.StateClass
import com.o7solutions.braingames.R
import com.o7solutions.braingames.auth.LoginActivity
import com.o7solutions.braingames.databinding.FragmentDashboard2Binding
import com.o7solutions.braingames.utils.AppFunctions

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentDashboard2Binding
    val viewModel: ProfileViewModel by lazy {
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo =
                    Repository(RetrofitClient.authInstance) // Use your singleton Repository instance
                return ProfileViewModel(repo) as T // <-- pass repo instead of apiService
            }
        }
        ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

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
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_dashboard2, container, false)
        binding = FragmentDashboard2Binding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RetrofitClient.setToken(requireActivity())
        Log.d("Dashboard Fragment Token", AppFunctions.getToken(requireActivity()).toString())

        viewModel.getUserById(AppFunctions.getUserId(requireActivity()).toString())
        initView()


        binding.viewGames.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.profileImageCard.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }

    }

    fun initView() {
        viewModel.userLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is StateClass.Error -> {
                    binding.pgBar.visibility = View.GONE
                    Toast.makeText(requireActivity(), "Internal Issue try refreshing app", Toast.LENGTH_SHORT).show()
                    Log.e("ProfileFragment", "Error: ${state.message}")
                }

                StateClass.Loading -> {
                    binding.pgBar.visibility = View.VISIBLE
                }

                is StateClass.Success<*> -> {
                    binding.pgBar.visibility = View.GONE

                    val user = state.data as UserResponse.UserData
                    AppFunctions.saveUser(requireActivity(), user)
                    Log.d("User Data", user.toString())
                    Log.d("Token from shared preference", AppFunctions.getToken(requireActivity()).toString())


                    if (user != null) {
                        val playTimeMinutes =
                            user.playTime / 60000  // Assuming playTime is in seconds
                        binding.tvTotalGames.text = user.totalGames.toString()
//                        binding.winRateValue.text = "${user.winRate}%"
//                        binding.streakValue.text = user.winStreak.toString()
//                        binding.tv.text = user.totalScore.toString()
                        binding.tvTotalTime.text = "$playTimeMinutes min"
                        binding.tvTotalWins.text = user.totalWins.toString()
                        binding.usernameText.text = "Hi,${user.name}"
//                        binding.levelText.text = "Level ${user.level}"
                        binding.streakTV.text = user.streak.count.toString()

                        AppFunctions.saveTips(requireActivity(), user.tips)


                        Log.d("ProfileFragment", "User loaded: ${user.name}")
                    } else {
                        Log.e("ProfileFragment", "User data is null or incorrect format")
                    }
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
         * @return A new instance of fragment DashboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}