package com.o7solutions.braingames.UI

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.o7solutions.braingames.DataClasses.OtpResponse
import com.o7solutions.braingames.DataClasses.OtpVerification
import com.o7solutions.braingames.Model.RetrofitClient
import com.o7solutions.braingames.R
import com.o7solutions.braingames.databinding.FragmentFirst2Binding
import kotlinx.coroutines.launch
import android.util.Log


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class First2Fragment : Fragment() {


//    Requesting OTP

    // This property is only valid between onCreateView and
    // onDestroyView.

    private lateinit var binding: FragmentFirst2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFirst2Binding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.buttonSubmit.setOnClickListener {
            binding.pgBar.visibility = View.VISIBLE

            val email = binding.editTextEmail.text.toString().trim()

            if (email.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        Log.d("Email",email)
                        val response = RetrofitClient.authInstance.requestOTP(email)

                        binding.pgBar.visibility = View.GONE

                        if (response.isSuccessful ) {
                            // Success case


                            var responseBody = response.body() as OtpResponse
                            Log.d("OTP",response.body()?.otp.toString())
                            Toast.makeText(
                                requireContext(),
                                "OTP sent successfully!",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Example: navigate to OTP verification screen
                            binding.editTextOTP.visibility = View.VISIBLE
                            binding.buttonSubmitOTP.visibility = View.VISIBLE
                            binding.buttonSubmit.visibility = View.GONE
                        } else {
                            // Failure case
                            Toast.makeText(
                                requireContext(),
                                "Failed to send OTP: ${response.message()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: Exception) {
                        binding.pgBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                binding.pgBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Please enter email!", Toast.LENGTH_SHORT).show()
            }
        }


        binding.buttonSubmitOTP.setOnClickListener {
            binding.pgBar.visibility = View.VISIBLE

            if (binding.editTextEmail.text.isEmpty()) {
                Toast.makeText(requireActivity(), "Email must not be empty", Toast.LENGTH_SHORT)
                    .show()
            } else if (binding.editTextOTP.text.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill OTP!", Toast.LENGTH_SHORT).show()
            } else {

                lifecycleScope.launch {


                    val response = RetrofitClient.authInstance.verifyOTP(
                        binding.editTextEmail.text.toString(),
                        binding.editTextOTP.text.toString().toInt()
                    )

                    if(response.isSuccessful){
                        binding.pgBar.visibility = View.GONE

//                        var responseBody = response.body() as OtpVerification

                        var bundle = Bundle()
                        bundle.putString("id",response.body()?.playerId)

                        findNavController().navigate(R.id.action_First2Fragment_to_SecondFragment,bundle)
                    } else {
                        binding.pgBar.visibility = View.GONE

                        Toast.makeText(requireContext(), "Invalid OTP!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }


}