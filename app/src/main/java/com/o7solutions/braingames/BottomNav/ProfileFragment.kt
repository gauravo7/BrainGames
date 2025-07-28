package com.o7solutions.braingames.BottomNav

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
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

        binding.logOutBTN.setOnClickListener {
            showLogOutDialog()
        }

        AppFunctions.getUserDataFromFirestore(auth.currentUser!!.email.toString()) { user ->

            if (user != null) {

                var playTimeHours: Float = user.playTime?.toFloat()!! / 60000
                binding.gamesPlayedValue.text = user.totalGames.toString()
                binding.winRateValue.text = "${user.winRate.toString()}%"
                binding.streakValue.text = user.winStreak.toString()
                binding.totalScoreValue.text = user.totalScore.toString()
                binding.playTimeValue.text = "${playTimeHours.toInt()} min"
                binding.totalWins.text = user.totalWins.toString()
                binding.usernameText.text = user.name.toString()
                Log.e("ProfileFragment",user.name.toString())
                binding.levelText.text = "Level${user.level}"

            }
        }
    }

    fun showLogOutDialog() {

        AlertDialog.Builder(context)
            .setTitle("Logged Out")
            .setMessage("You are already logged out.")
            .setPositiveButton("OK") { dialog, _ ->
                auth.signOut()
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