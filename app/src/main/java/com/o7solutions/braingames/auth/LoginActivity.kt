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
import com.o7solutions.braingames.BottomNav.BottomNavActivity
import com.o7solutions.braingames.DataClasses.Auth.LoginRequest
import com.o7solutions.braingames.DataClasses.Auth.LoginRequest.LoginResponse
import com.o7solutions.braingames.DataClasses.Auth.UserResponse
import com.o7solutions.braingames.HomeScreens.HomeScreenActivity
import com.o7solutions.braingames.Model.RetrofitClient
import com.o7solutions.braingames.R
import com.o7solutions.braingames.UI.ForgotPasswordActivity
import com.o7solutions.braingames.databinding.ActivityLoginBinding
import com.o7solutions.braingames.utils.AppConstants
import com.o7solutions.braingames.utils.AppFunctions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textViewForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        binding.buttonLogin.setOnClickListener {
//            val intent = Intent(this, BottomNavActivity::class.java)
//            startActivity(intent)
//            finish()
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                binding.progressContainer.visibility = View.VISIBLE

                loginUserWithApi(email,password)

//                loginUser(email, password)
            }
        }

        binding.textViewSignUp.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun loginUserWithApi(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)
        val call = RetrofitClient.instance.loginUser(loginRequest)

        call.enqueue(object : Callback<LoginRequest.LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse?>,
                response: Response<LoginResponse?>
            ) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.success) {
                        val token = loginResponse.token
                        val id = loginResponse.data._id
                        val userData = loginResponse.data
                        val tips = userData.tips
//                        AppFunctions.saveUser(this@LoginActivity,userData as UserResponse.UserData)

                        // Save token if needed
//                        val sharedPref = getSharedPreferences(AppConstants.userPref, MODE_PRIVATE)
//                        sharedPref.edit().putString(AppConstants.token, token).apply()

                        AppFunctions.saveToken(this@LoginActivity,token)
                        Log.d("Token get",token.toString())
                        Log.d("Token from shared preference", AppFunctions.getToken(this@LoginActivity).toString())
                        AppFunctions.saveUserId(this@LoginActivity,id)
                        AppFunctions.saveTips(this@LoginActivity,tips)
                        Log.d("User Token", AppFunctions.getToken(this@LoginActivity).toString())

                        AppFunctions.fetchUserData(AppFunctions.getUserId(this@LoginActivity).toString(),this@LoginActivity)
                        // Show success message or navigate
                        Toast.makeText(this@LoginActivity, "Welcome ${userData.name}", Toast.LENGTH_SHORT).show()


                        binding.progressContainer.visibility = View.GONE
                         startActivity(Intent(this@LoginActivity, HomeScreenActivity::class.java))
                        finish()
                    } else {
                        binding.progressContainer.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, "Login failed: ${loginResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    binding.progressContainer.visibility = View.GONE
                    Toast.makeText(this@LoginActivity, "Server Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                call: Call<LoginResponse?>,
                t: Throwable
            ) {
                Toast.makeText(this@LoginActivity, "Login Failed: $t", Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun loginUser(email: String, password: String) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.progressContainer.visibility = View.GONE

                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, BottomNavActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    AppFunctions.showAlert(task.exception?.localizedMessage.toString(),this)
                }
            }
    }
}