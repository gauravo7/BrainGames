package com.o7solutions.braingames.UI

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.o7solutions.braingames.Model.RetrofitClient
import com.o7solutions.braingames.auth.LoginActivity
import com.o7solutions.braingames.databinding.FragmentSecondBinding
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

   private lateinit var binding : FragmentSecondBinding
   var id = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            id = it.getString("id").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSubmitPassword.setOnClickListener {
            if(binding.editTextNewPassword.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please enter password!", Toast.LENGTH_SHORT).show()
            }  else if(binding.editTextNewPassword.text.toString().length < 8) {
                Toast.makeText(requireContext(), "Please fill at least 8 characters", Toast.LENGTH_SHORT).show()

            }  else{
                lifecycleScope.launch {
                    val response = RetrofitClient.authInstance.changePassword(id,binding.editTextNewPassword.text.toString())

                    if(response.isSuccessful) {
                        val intent = Intent(requireActivity(), LoginActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                     else {
                        Toast.makeText(requireContext(), "Unable to change password!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }


}