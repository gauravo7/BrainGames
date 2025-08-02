package com.o7solutions.braingames.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.o7solutions.braingames.BottomNav.BottomNavActivity
import com.o7solutions.braingames.DataClasses.Auth.RegisterResponse
import com.o7solutions.braingames.DataClasses.Users
import com.o7solutions.braingames.Model.ApiService
import com.o7solutions.braingames.Model.RetrofitClient
import com.o7solutions.braingames.R
import com.o7solutions.braingames.databinding.ActivitySignupBinding
import com.o7solutions.braingames.utils.AppConstants
import com.o7solutions.braingames.utils.AppFunctions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding= ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.buttonSignUp.setOnClickListener {
            val name = binding.editTextFullName.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            val confirmPassword = binding.editTextConfirmPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showToast("Please fill in all fields")
                return@setOnClickListener
            }

            if (password.length < 8) {
                showToast("Password must be at least 8 characters")
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                showToast("Passwords do not match")
                return@setOnClickListener
            }

            registerUserWithApi(name,email,password)
//            registerUser(name,email, password)
        }

        binding.textViewSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUserWithApi(name: String,email: String, password: String) {
        val call = RetrofitClient.instance.registerUser(name, email, password)

        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val res = response.body()
                    Toast.makeText(this@SignupActivity, "Registered: ${res?.message}", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@SignupActivity, BottomNavActivity::class.java)
                    startActivity(intent)
                    finish()


                    Log.d("REGISTER", "Token: ${res?.token}")
                } else {
                    Toast.makeText(this@SignupActivity, "Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("Register Error",response.code().toString())
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@SignupActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun registerUser(name: String,email: String, password: String) {
        binding.buttonSignUp.isEnabled = false

        binding.progressContainer.visibility = View.VISIBLE
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.buttonSignUp.isEnabled = true
                if (task.isSuccessful) {
                    createUser(name,email,password)
                } else {
                    AppFunctions.showAlert(task.exception?.localizedMessage.toString(),this)
                }
            }

        binding.progressContainer.visibility = View.GONE
    }

    fun createUser(name: String,email: String, password: String) {
        val newUser = Users(name,email,0,0,0,0.0f,0,0,0,0, ArrayList())
        db.collection(AppConstants.user).document(firebaseAuth.currentUser?.email.toString())
            .set(newUser)
            .addOnSuccessListener {
                showToast("Account created successfully")
                firebaseAuth.currentUser?.sendEmailVerification()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener { e->

                AppFunctions.showAlert(e.localizedMessage.toString(),this)
            }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}