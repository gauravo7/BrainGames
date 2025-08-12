package com.o7solutions.braingames.BottomNav

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.o7solutions.braingames.BottomNav.ViewModel.HomeViewModel
import com.o7solutions.braingames.BottomNav.ViewModel.ProfileViewModel
import com.o7solutions.braingames.DataClasses.Auth.UserResponse
import com.o7solutions.braingames.Model.Repository
import com.o7solutions.braingames.Model.RetrofitClient
import com.o7solutions.braingames.Model.StateClass
import com.o7solutions.braingames.R
import com.o7solutions.braingames.auth.LoginActivity
import com.o7solutions.braingames.databinding.FragmentProfileBinding
import com.o7solutions.braingames.utils.AppFunctions

/**
 * A simple [androidx.fragment.app.Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    val viewModel: ProfileViewModel by lazy {
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = Repository(RetrofitClient.authInstance) // Use your singleton Repository instance
                return ProfileViewModel(repo) as T // <-- pass repo instead of apiService
            }
        }
        ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
//        binding.pgBar.visibility = View.VISIBLE

        binding.logOutBTN.setOnClickListener {
            showLogOutDialog()
        }


//        viewModel.userLiveData.observe {  }
        viewModel.getUserById(AppFunctions.getUserId(requireActivity()).toString())


//        val user = AppFunctions.getUser(requireActivity())
//        AppFunctions.saveUser(requireActivity(),user)
//        Log.d("User Data",user.toString())


//        if (user != null) {
//            val playTimeMinutes = user.playTime / 60000  // Assuming playTime is in seconds
//            binding.gamesPlayedValue.text = user.totalGames.toString()
//            binding.winRateValue.text = "${user.winRate}%"
//            binding.streakValue.text = user.winStreak.toString()
//            binding.totalScoreValue.text = user.totalScore.toString()
//            binding.playTimeValue.text = "$playTimeMinutes min"
//            binding.totalWins.text = user.totalWins.toString()
//            binding.usernameText.text = user.name
//            binding.levelText.text = "Level ${user.level}"
//
//            Log.d("ProfileFragment", "User loaded: ${user.name}")
//        } else {
//            Log.e("ProfileFragment", "User data is null or incorrect format")
//        }
        initView()
//        Updating Streak
//        AppFunctions.getStreak { streak->
//            binding.streakTV.text = streak.count.toString()
//        }


//        AppFunctions.getUserDataFromFirestore(auth.currentUser!!.email.toString()) { user ->
//
//            if (user != null) {
//
//                var playTimeHours: Float = user.playTime?.toFloat()!! / 60000
//                binding.gamesPlayedValue.text = user.totalGames.toString()
//                binding.winRateValue.text = "${user.winRate.toString()}%"
//                binding.streakValue.text = user.winStreak.toString()
//                binding.totalScoreValue.text = user.totalScore.toString()
//                binding.playTimeValue.text = "${playTimeHours.toInt()} min"
//                binding.totalWins.text = user.totalWins.toString()
//                binding.usernameText.text = user.name.toString()
//                Log.e("ProfileFragment",user.name.toString())
//                binding.levelText.text = "Level${user.level}"
//
//                binding.pgBar.visibility = View.GONE
//            }
//        }
    }

    fun initView() {
        viewModel.userLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is StateClass.Error -> {
                    binding.pgBar.visibility = View.GONE
                    Log.e("ProfileFragment", "Error: ${state.message}")
                }

                StateClass.Loading -> {
                    binding.pgBar.visibility = View.VISIBLE
                }

                is StateClass.Success<*> -> {
                    binding.pgBar.visibility = View.GONE

                    val user = state.data as UserResponse.UserData
                    AppFunctions.saveUser(requireActivity(),user)
                    Log.d("User Data",user.toString())


                    if (user != null) {
                        val playTimeMinutes = user.playTime / 60000  // Assuming playTime is in seconds
                        binding.gamesPlayedValue.text = user.totalGames.toString()
                        binding.winRateValue.text = "${user.winRate}%"
                        binding.streakValue.text = user.winStreak.toString()
                        binding.totalScoreValue.text = user.totalScore.toString()
                        binding.playTimeValue.text = "$playTimeMinutes min"
                        binding.totalWins.text = user.totalWins.toString()
                        binding.usernameText.text = user.name
                        binding.levelText.text = "Level ${user.level}"
                        binding.streakTV.text = user.streak.count.toString()

                        AppFunctions.saveTips(requireActivity(),user.tips)


                        Log.d("ProfileFragment", "User loaded: ${user.name}")
                    } else {
                        Log.e("ProfileFragment", "User data is null or incorrect format")
                    }
                }
            }
        }
    }


    fun showLogOutDialog() {

        AlertDialog.Builder(context)
            .setTitle("Logged Out")
            .setMessage("You are already logged out.")
            .setPositiveButton("OK") { dialog, _ ->
                auth.signOut()
                AppFunctions.deleteToken(requireActivity())
                AppFunctions.deleteUserId(requireActivity())
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
                dialog.dismiss()
            }
            .show()

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}